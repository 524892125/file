# Welcome to the Dev Tools Console!
#
# You can use Console to explore the Elasticsearch API. See the Elasticsearch API reference to learn more:
# https://www.elastic.co/guide/en/elasticsearch/reference/current/rest-apis.html
#
# Here are a few examples to get you started.


PUT /user_login
{
"mappings": {
"properties": {
"_class": {
"type": "keyword",
"index": false,
"doc_values": false
},
"account": {
"type": "text",
"fields": {
"keyword": {
"type": "keyword",
"ignore_above": 256
}
},
"fielddata": true
},
"date": {
"type": "text",
"fields": {
"keyword": {
"type": "keyword",
"ignore_above": 256
}
}
},
"timestamp": {
"type": "date"
},
"type": {
"type": "long"
}
}
}
}


# Create an index
PUT /user_login/_mapping
{
"properties": {
"account": {
"type": "text",
"fielddata": true
}
}
}

GET /user_login/_mapping

PUT /user_login/_mapping
{
"properties": {
"timestamp": {
"type": "date",
"format": "epoch_millis"  // 使用秒级时间戳
}
}
}


DELETE /user_login

POST /user_login/_search
{
"size": 0,
"aggs": {
"daily_login_count": {
"date_histogram": {
"field": "timestamp",
"calendar_interval": "day",
"format": "yyyy-MM-dd"  // Convert timestamp to date format (YYYY-MM-DD)
},
"aggs": {
"by_account": {
"terms": {
"field": "account",
"size": 100000
}
}
}
}
}
}

POST /user_login/_search
{
"size": 0,
"aggs": {
"daily_login_count": {
"date_histogram": {
"field": "timestamp",
"calendar_interval": "day",
"format": "yyyy-MM-dd"  // 将日期格式化为 'yyyy-MM-dd'

      },
      "aggs": {
        "by_account": {
          "terms": {
            "field": "account",
            "size": 10000
          }
        }
      }
    }
}
}



POST /user_login/_search
POST /user_login/_search
{
"query": {
"bool": {
"must": [
{
"range": {
"type": {
"gte": 1,
"lte": 2
}
}
},
{
"terms": {
"account": ["100800615"]  // Replace with your account values
}
}
]
}
},
"aggs": {
"by_date": {
"terms": {
"script": {
"source": "doc['date'].value.substring(0, 10)",  // Extracts the first 10 characters (yyyy-MM-dd)
"lang": "painless"
},
"size": 10  // Limit the aggregation to the top 10 dates
}
}
}
}




POST /user_login/_search
{
"query": {
"bool": {
"must": [
{
"range": {
"type": {
"gte": 1,
"lte": 2
}
}
}
]
}
},
"aggs": {
"by_date": {
"date_histogram": {
"field": "timestamp",
"fixed_interval": "1d",  // 这里按天聚合
"format": "yyyy-MM-dd"  // 格式化为年月日
}
}
}
}




# Add a document to my-index
POST /user_login/_doc
{
"id": "park_rocky-mountain",
"title": "Rocky Mountain",
"description": "Bisected north to south by the Continental Divide, this portion of the Rockies has ecosystems varying from over 150 riparian lakes to montane and subalpine forests to treeless alpine tundra."
}


# Perform a search in my-index
GET /my-index/_search?q="rocky mountain"