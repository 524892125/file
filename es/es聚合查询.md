```json
# 查询所有球员的平均年龄是多少，并对球员的平均薪水加 188（也可以理解为每名球员加 188 后的平均薪水）。

POST student/_search?size=0
{
  "aggs": {
    "avg_age": {
      "avg": {
        "field": "age"
      }
    },
    "avg_age_188": {
      "avg": {
        "script": {
          "source": "doc.age.value + 188"
        }
      }
    }
  }
}
```