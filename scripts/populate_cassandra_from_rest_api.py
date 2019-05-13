#!/usr/bin/python

import requests
import cassandra
import time
from cassandra.cluster import Cluster
import uuid

cluster = Cluster(['192.168.99.100'], port=9045)

session = cluster.connect('juke_box_system')

resp_settings = requests.get('http://my-json-server.typicode.com/touchtunes/tech-assignment/settings')

#print("{}".format(resp_settings.text))
if resp_settings.status_code != 200:
    # This means something went wrong.
    raise ApiError('GET {}'.format(resp_settings.status_code))

settings = {}

for setting in resp_settings.json()['settings']:
    settings[uuid.UUID(setting['id'])] = set(setting['requires'])
    for component in setting['requires']:
        session.execute(
            """
            INSERT INTO settings (setting_id, component)
            VALUES (%s, %s) IF NOT EXISTS
            """,
            (uuid.UUID(setting['id']), component)
        )


resp = requests.get('http://my-json-server.typicode.com/touchtunes/tech-assignment/jukes')

#print(settings)
if resp.status_code != 200:
    # This means something went wrong.
    raise ApiError('GET {}'.format(resp.status_code))

for juke in resp.json():
    # print('{} {} {}'.format(juke['id'], juke['model'], juke['components']))
    arr = []
    for component in juke['components']:
        arr.append(component['name'])
    for s_id in settings.keys():
        intersect = settings[s_id].intersection(set(arr))
        len_required_component = len(settings[s_id])
        if (len(intersect) == len(settings[s_id])):
            print(settings[s_id], set(arr), intersect)
            session.execute(
                """
                INSERT INTO juke_boxes (model, setting, id, components)
                VALUES (%s, %s, %s, %s) IF NOT EXISTS
                """,
                (juke['model'], s_id, juke['id'], set(arr))
            )

