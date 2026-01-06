# coding=utf-8
import json
import time
import mysql.connector as cpy
config = {
    'user': 'www_kiifstudio_c',
    'password': 'GjT7NNLHByBhBTz4',
    'host': 'localhost',
    'database': 'www_kiifstudio_c',
    "port": 3306,
    "use_pure": True,
}
# config = {
#     'user': 'root',
#     'password': '123456',
#     'host': '192.168.50.65',
#     'database': 'www_kiifstudio_c',
#     "port": 3306,
#     "use_pure": True,
# }

with cpy.connect(**config) as cnx:
    with cnx.cursor(buffered=True) as cur:
        start_time = time.time()

        cur.execute("SELECT id,option_name,option_value from tlk_option where option_name = 'site_info'")
        res = cur.fetchone()
        if not res:
            raise Exception("未找到 site_info")

        option_value = res[2]
        json_option_data = json.loads(option_value)
        json_option_data['site_down_url'] = 'https://ember.kiifstudio.com/ase_release_1.3.0_0.apk'
        json_option_data['site_seo_description'] = 'https://ember.kiifstudio.com/ase_release_1.3.0_0.apk'

        update_query = """
        UPDATE tlk_option SET option_value = %s WHERE option_name = 'site_info'
        """
        cur.execute(update_query,  (json.dumps(json_option_data),))

        cnx.commit()

        end_time = time.time()
        print(f"更新条数据耗时: {end_time - start_time:.2f}秒")

    cur.close()
    cnx.close()
