import requests
import json
import subprocess
from collections import defaultdict
from pymongo_get_database import get_database
import time

Sum = 0
   
while True: 
    Sum += 1
    dbname = get_database()
    collection_name = dbname["DBArticles"]

    params = {
    'sources': 'bbc-news',
    'apiKey': 'da96c59f99244bbf90b6269dc3c90a20',
    }

    response = requests.get('https://newsapi.org/v2/top-headlines', params=params)
    if response.status_code != 200:
        print("API call failed. Response: " + response.status_code)


    todos = json.loads(response.text) 
    #parse the response
    todos == response.json()

    #isolate the relevant data
    data = todos.get("articles")

    print(type(data)) #arraylist

    print(len(data))  #10

    #print(data[0].get("description"))

    #class to represent the data
    class Article:

        title =''
        url =''
        description =''
        publishedAt =''
        content =''
        
        def __init__(self, title, url, description, publishedAt, content):
            self.title = title
            self.url = url
            self.description = description
            self.publishedAt = publishedAt
            self.content = content

    list = []


    # construct list of objects based on response
    for i in data:
        #print(i.get("title"))
        title = i.get("title")
        url = i.get("url")
        #print(i.get("description"))
        description = i.get("description")
       # print(i.get("publishedAt"))
        publishedAt = i.get("publishedAt")
       # print(i.get("content"))
        content = i.get("content")

        list.append(Article(title, url, description, publishedAt, content))
    #print(todos.values())

        #for every object in list, create corresponding database document
    for i in list:
        print(i.title)
    collection_name.delete_many({})
    for i in list:
        print(i.description)
        n = i.title
        n = {
      "title" : i.title,
      "url" : i.url,
      "description" : i.description,
      "publishedAt" : i.publishedAt,
      "content" : i.content,
    }
        collection_name.insert_one(n)

    print("\n")

    #second list to contain collecion (cursor) pulled from DB
    list2 = []


    fromdb = collection_name.find({})
    print(fromdb.count())
    #if database collection is already populated
    if fromdb.count() ==10:
        #get contents of collection for comparasion
        for document in fromdb:
            #print (document["description"])
            title = document["title"]
            url = document["url"]
            description = document["description"]
            publishedAt = document["publishedAt"]
            content = document["content"]

            list2.append(Article(title, url, description, publishedAt, content))
    #print(type(fromdb)) # cursor

    print("Operations completed: "+str(Sum)+" times.")
    time.sleep(60)

