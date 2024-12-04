https://www.jianshu.com/p/0959d2221f43



资源
本教程译自 mongocxx 官方 Tutorial for mongocxx。译者这里根据官方教程，加入了自己的理解和代码。更新时间：2018年9月25日。

准备条件
一个已经运行在本地（localhost）27017端口（port）的 mongodb 实例（instance）。
mongocxx 驱动，请参阅 Installation for mongocxx。这里 是关于该教程的翻译。
在你的源文件下包括以下声明：
#include <cstdint>
#include <iostream>
#include <vector>
#include <bsoncxx/json.hpp>
#include <mongocxx/client.hpp>
#include <mongocxx/stdx.hpp>
#include <mongocxx/uri.hpp>

using bsoncxx::builder::stream::close_array;
using bsoncxx::builder::stream::close_document;
using bsoncxx::builder::stream::document;
using bsoncxx::builder::stream::finalize;
using bsoncxx::builder::stream::open_array;
using bsoncxx::builder::stream::open_document;
我这里在编译的过程中会出现编译错误，经查证还应该包含以下头文件：

// 官方文档中缺少以下语句
#include <bsoncxx\builder\stream\document.hpp>       
#include <mongocxx\instance.hpp>
编译
在安装 mongocxx 驱动的过程中会安装一个 libmongocxx.pc 文件，用于配合 pkg-config 使用。编译程序时请使用以下命令：

c++ --std=c++11 <input>.cpp $(pkg-config --cflags --libs libmongocxx)
如果你不使用 pkg-config，你需要手动的在命令行或者你的 IDE 中设置头文件和库文件路径。假设 libmongoc 和 mongocxx 安装在 /usr/local，那么应该使用下面这样的命令去编译你的代码：

c++ --std=c++11 <input>.cpp
-I/usr/local/include/mongocxx/v_noabi -I/usr/local/include/libmongoc-1.0 \
-I/usr/local/include/bsoncxx/v_noabi -I/usr/local/include/libbson-1.0 \
-L/usr/local/lib -lmongocxx -lbsoncxx
译者这里编写的例程相对于官方例程做了一些完善和补充，选择的编译方式为 CMake 构建，VS 编译调试运行。可以参考 mongodb c++ 驱动 中的构建方式，同时文章最后也会给出本文编译所用的 CMakeLists.txt 文件以及命令行构建参数。

建立连接
重要：在建立任何连接之前，你需要创建且仅创建一个 mongocxx::instance 实例。这个实例必须在你整个程序周期中都存在。

请使用 mongocxx::client 类去连接一个正在运行的 mongoDB 实例。

你必须使用一个包含 MongoDB URI 的 mongocxx:uri 实例指定待连接的主机（host），然后将其传递给 mongocxx::client的构造函数。

mongocxx::uri 的默认构造函数会连接到运行在 localhost 27017 端口的服务：

mongocxx::instance instance{}; // This should be done only once.
mongocxx::client client{mongocxx::uri{}};
上面代码等同于：

mongocxx::instance instance{}; // This should be done only once.
mongocxx::uri uri("mongodb://localhost:27017");
mongocxx::client client(uri);
访问数据库（Database）
一旦你有了一个连接到 MongoDB 服务（原文为 deployment）的 mongocxx::client实例。你就可以使用 database() 方法或者 [] 操作获得一个 mongocxx::database 实例。

如果你访问的数据库不存在，MongoDB 会在你第一次存储数据时创建这个数据库。

下面这个例子演示了方位 mydb 数据库：

mongocxx::database db = client["mydb"];
访问一个集合（Collection）
一旦你有了一个 mongocxx::database 实例，你就可以使用 collection() 方法或者 [] 操作获得一个 mongocxx::collection 实例。

如果你访问的集合不存在，MongoDB 会在你第一次存储数据时创建这个集合。

例如，使用之前段落中创建的 db 实例，下面代码会访问 mydb 中的 test 集合：

mongocxx::collection coll = db["test"];
创建一个文档（Document）
为了使用 C++ 驱动创建一个文档，需要在两个可用的生成器接口（builder interfaces）中选择一个使用：

流生成器（Stream builder）：bsoncxx::builder::stream 一个使用流操作的文档生成器，在构建文字文档时表现较好。

基本生成器（Basic builder）：bsoncxx::builder::basic 生成器实例中更加方便的文档生成器，包含了一些调用方法。

本教程仅简短的对流生成器进行介绍。

例如：考虑如下的一个 JSON 文档：

{
"name" : "MongoDB",
"type" : "database",
"count" : 1,
"versions": [ "v3.2", "v3.0", "v2.6" ],
"info" : {
"x" : 203,
"y" : 102
}
}
使用流生成器构建接口，你可以使用下面代码构建这个文档：

auto builder = bsoncxx::builder::stream::document{};
bsoncxx::document::value doc_value = builder
<< "name" << "MongoDB"
<< "type" << "database"
<< "count" << 1
<< "versions" << bsoncxx::builder::stream::open_array
<< "v3.2" << "v3.0" << "v2.6"
<< close_array
<< "info" << bsoncxx::builder::stream::open_document
<< "x" << 203
<< "y" << 102
<< bsoncxx::builder::stream::close_document
<< bsoncxx::builder::stream::finalize;
使用 bsoncxx::builder::stream::finalize 获得一个 bsoncxx::document::value 的实例。

bsoncxx::document::value 类型是一个拥有独立存储的只读对象。为了使用它，你必须使用其 view() 方法获得一个 bsoncxx::document::view 对象：

bsoncxx::document::view view = doc_value.view();
你可以使用 []操作 访问文档中的字段，这将会返回一个 bsoncxx::document::element 实例。例如，下面代码将会提取类型为字符串的 name 字段：

bsoncxx::document::element element = view["name"];
if(element.type() != bsoncxx::type::k_utf8) {
// Error
}
std::string name = element.get_utf8().value.to_string();
如果 name 中的值不是字符串且你没有在上面代码中包含类型检查，这个代码将会抛出 bsoncxx::exception 实例。

对于以上官方文档的介绍，译者这里编写对应的完整演示代码如下。该代码较官方文档，增加了访问数组（array）和文档（document）字段的方法。对于文档的其他数据类型的访问，如布尔字段、时间字段，直接参考官方的源码即可（数据类型都属于同一个枚举且访问方式类似）。

/*  @file access.cpp
@brief 本代码演示了如何访问 mongodb 并创建 JSON 文档

    1. 因为不会向数据库中插入内容，所以不会创建数据库、集合和文档（mongodb 在插入数据时自动创建对应数据库）。
    2. 演示的 JSON document 的创建和访问。方式不唯一，这里只是使用了其中的一种方式。
    3. 运行程序，会在屏幕上打印 JSON 文档的部分内容（重复的访问方式不再列举）。
*/

#include <cstdint>
#include <iostream>
#include <vector>
#include <bsoncxx/json.hpp>
#include <mongocxx/client.hpp>
#include <mongocxx/stdx.hpp>
#include <mongocxx/uri.hpp>
// 官方文档中缺少以下语句
#include <bsoncxx\builder\stream\document.hpp>       
#include <mongocxx\instance.hpp>

using bsoncxx::builder::stream::close_array;
using bsoncxx::builder::stream::close_document;
using bsoncxx::builder::stream::document;
using bsoncxx::builder::stream::finalize;
using bsoncxx::builder::stream::open_array;
using bsoncxx::builder::stream::open_document;

int main(int,char**)
{
mongocxx::instance instance{};      // This should be done only once.
//mongocxx::client client{ mongocxx::uri{} };   
// 上面代码等同于:
mongocxx::uri uri("mongodb://localhost:27017");
mongocxx::client client(uri);

    mongocxx::database db = client["mydb"];     // 访问数据库
    mongocxx::collection coll = db["test"];     // 访问集合

    // 构造 JSON 文档
    auto builder = bsoncxx::builder::stream::document{};
    bsoncxx::document::value doc_value = builder
        << "name" << "MongoDB"
        << "type" << "database"
        << "count" << 1
        << "versions" << bsoncxx::builder::stream::open_array
        << "v3.2" << "v3.0" << "v2.6"
        << close_array
        << "info" << bsoncxx::builder::stream::open_document
        << "x" << 203
        << "y" << 102
        << bsoncxx::builder::stream::close_document
        << bsoncxx::builder::stream::finalize;
    // 访问 JSON 文档 string 字段
    bsoncxx::document::view view = doc_value.view();
    bsoncxx::document::element element = view["name"];
    if (element.type() != bsoncxx::type::k_utf8)
    {
        std::cout << "name type error." << std::endl;
    }
    std::string name = element.get_utf8().value.to_string();
    std::cout << "name:" << std::endl;
    std::cout << name << std::endl;
    // 访问 JSON 文档 array 字段
    element = view["versions"];
    if (element.type() != bsoncxx::type::k_array)
    {
        std::cout << "versions type error." << std::endl;
    }
    bsoncxx::array::view version = element.get_array().value;       // 获取到的元素为 array view
    std::cout << "version:" << std::endl;
    for each (bsoncxx::array::element var in version)
    {
        std::string value = var.get_utf8().value.to_string();       // 此处不再进行类型检查,检查是否为 string 的方法和上面相同
        std::cout << value<<" ";
    }
    std::cout << std::endl;
    // 访问 JSON 文档 document 字段
    element = view["info"];
    if (element.type() != bsoncxx::type::k_document)
    {
        std::cout << "info type error." << std::endl;
    }
    bsoncxx::document::view info = element.get_document().value;        // 获取到的元素依然为 document view
    std::cout << "info:" << std::endl;
    bsoncxx::document::element ele1 = info["x"];
    if (ele1.type() != bsoncxx::type::k_int32)
        std::cout << "ele1 type error" << std::endl;
    int x = ele1.get_int32().value;
    std::cout << "x: " << x << std::endl;
    int y = info["y"].get_int32().value;    // 如果不做参数类型检查，可以使用这种简单的方式
    std::cout << "y: " << y << std::endl;

    system("pause");
    return 0;
}
上面的代码看起来比较复杂，是因为我这里为了清楚的表名每个步骤的数据类型，并没有使用 auto 关键字。如果我们使用到 auto 关键字且不做参数类型检查（或将代码放到 try 中）可以大幅简化代码。使用新的 c++ 标准语法会使我们的代码更加像高级语言，这也是 c++ 标准定制过程的趋势。

插入文档
插入单个文档
对于在集合中插入单个文档，请使用 mongocxx::collection 实例的 insert_one() 方法：

bsoncxx::stdx::optional<mongocxx::result::insert_one> result =
restaurants.insert_one(doc);
如果你没有在文档中手动指定 _id 字段，MongoDB 会自动的在插入的文档中添加一个 _id字段。你可以通过返回的 mongocxx::result::insert_one 实例的 inserted_id() 方法获得这个返回的值。

插入多个文档
对于在集合中插入多个文档，请使用 mongocxx::collection 实例的 insert_many() 方法，该方法可以插入一个文档列表。

接下来的代码会插入多个下面形式的文档：

{ "i" : value }
创建一个文档列表并在循环中添加文档：

std::vector<bsoncxx::document::value> documents;
for(int i = 0; i < 100; i++) {
documents.push_back(
bsoncxx::builder::stream::document{} << "i" << i << finalize);
}
为了将文档列表插入到集合中，应该将文档列表传递给 insert_many() 方法。

collection.insert_many(documents);
如果你不给每一个文档指定 _id 字段，MongoDB 会自动的给插入的文档添加 _id 字段。

你可以通过返回的 mongocxx::result::insert_many 实例的 inserted_ids() 方法获得这些值。

集合查询（Query）
为了查询集合，请使用集合的 find() 和 find_one() 方法。

find() 方法将返回一个 mongocxx::cursor 实例，对应的 find_one 将返回一个 std::optional< bsoncxx::document::value > 实例。

你即可以通过调用一个传递了空文档的方法来查询集合中的所有文档，也可以传递一个过滤器（filter）来查询符合过滤条件的文档。

在集合中查询单个文档
为了获得集合中的一个文档，请使用不带任何参数的 find_one() 方法。

bsoncxx::stdx::optional<bsoncxx::document::value> maybe_result =
collection.find_one({});
if(maybe_result) {
// Do something with *maybe_result;
}
查询集合中的所有文档
mongocxx::cursor cursor = collection.find({});
for(auto doc : cursor) {
std::cout << bsoncxx::to_json(doc) << "\n";
}
指定一个查询过滤器
获取匹配过滤器的单个文档

为了获取字段 i 为 71 的第一个文档，传递文档 {"i":71} 来指定等式条件：

bsoncxx::stdx::optional<bsoncxx::document::value> maybe_result =
collection.find_one(document{} << "i" << 71 << finalize);
if(maybe_result) {
std::cout << bsoncxx::to_json(*maybe_result) << "\n";
}
这里例子将会输出一个文档：

{ "_id" : { "$oid" : "5755e19b38c96f1fb25667a8" },  "i" : 71 }
_id 字段是被 MongoDB 自动添加上的，你的值将会和上面显示的不同。MongoDB 保留字段名称以一个下划线（_）和美元符号（$）开始，用于内部使用。

获得匹配过滤器的所有文档

下面例子将会返回并打印满足 50<"i"<=100 条件的所有文档：

mongocxx::cursor cursor = collection.find(
document{} << "i" << open_document <<
"$gt" << 50 <<
"$lte" << 100
<< close_document << finalize);
for(auto doc : cursor) {
std::cout << bsoncxx::to_json(doc) << "\n";
}
对于以上官网给出的插入和查询示例，我编写了一个完整的代码。下面代码较官方的简单示例做了更多的补充，对 mongodb 不是太了解或还不是很了解 mongocxx 驱动的用户可能看起来比较吃力。

/*  @file access.cpp
@brief 本代码演示了如何插入及查询数据

1. 连接到 localhost 主机 27017 端口的 mongodb
2. 创建 mydb 数据库、test 集合
3. 使用单次和多次插值函数插入文档，并将插入文档的 _id 字段打印到屏幕
4. 使用单次查询，并输出查询结果的 id，使用多次查询输出所有查询结果
5. 使用带过滤器的查询，重复以上操作。
   */

#include <cstdint>
#include <iostream>
#include <vector>
#include <bsoncxx/json.hpp>
#include <mongocxx/client.hpp>
#include <mongocxx/stdx.hpp>
#include <mongocxx/uri.hpp>
// 官方文档中缺少以下语句
#include <bsoncxx\builder\stream\document.hpp>       
#include <mongocxx\instance.hpp>

using bsoncxx::builder::stream::close_array;
using bsoncxx::builder::stream::close_document;
using bsoncxx::builder::stream::document;
using bsoncxx::builder::stream::finalize;
using bsoncxx::builder::stream::open_array;
using bsoncxx::builder::stream::open_document;

int main(int, char**)
{
mongocxx::instance instance{};      // This should be done only once.
mongocxx::client client{ mongocxx::uri{} };


    mongocxx::database db = client["mydb"];     // 访问数据库
    mongocxx::collection coll = db["test"];     // 访问集合
    db.drop();                                  // 删除之前的记录

    // 构造 JSON 文档
    auto builder = bsoncxx::builder::stream::document{};
    bsoncxx::document::value doc_value = builder
        << "name" << "MongoDB"
        << "type" << "database"
        << "count" << 1
        << "versions" << bsoncxx::builder::stream::open_array
        << "v3.2" << "v3.0" << "v2.6"
        << close_array
        << "info" << bsoncxx::builder::stream::open_document
        << "x" << 203
        << "y" << 102
        << bsoncxx::builder::stream::close_document
        << bsoncxx::builder::stream::finalize;
    // 插入单条文档
    bsoncxx::stdx::optional<mongocxx::result::insert_one> result = coll.insert_one(doc_value.view());
    bsoncxx::oid oid = result->inserted_id().get_oid().value;
    std::string insertedId = oid.to_string();
    std::cout << "Insert one document,return id is " << insertedId << std::endl;
    // 插入多条文档
    std::vector<bsoncxx::document::value> documents;
    for (int i = 0; i < 100; ++i)
    {
        documents.push_back(
            bsoncxx::builder::stream::document{} << "i" << i << finalize);
    }
    bsoncxx::stdx::optional<mongocxx::result::insert_many> results = coll.insert_many(documents);
    std::int32_t insertNum = results->inserted_count();
    mongocxx::result::insert_many::id_map idMap = results->inserted_ids();
    for (auto ele : idMap)
    {
        auto idx = ele.first;
        auto id = ele.second.get_oid().value.to_string();
        std::cout << "index: " << idx << " -- id: " << id << std::endl;
    }

    // 查询单个文档
    bsoncxx::stdx::optional<bsoncxx::document::value> maybe_result = coll.find_one({});
    if (maybe_result)
    {
        bsoncxx::document::view view = maybe_result->view();
        auto findOneId = view["_id"].get_oid().value.to_string();
        std::cout << "find_one() return document id is " << findOneId << std::endl;
    }
    // 查询多个文档
    std::cout << "find() return values:" << std::endl;
    mongocxx::cursor cursor = coll.find({});
    for (bsoncxx::document::view docView : cursor)
    {
        std::cout << bsoncxx::to_json(docView) << "\n";
    }
    // 查询匹配过滤器的单个文档
    bsoncxx::stdx::optional<bsoncxx::document::value> maybeResult =
        coll.find_one(document{} << "i" << 71 << finalize);
    if (maybeResult)
    {
        std::cout << "specify query filter,find_one() return values:" << std::endl;
        std::cout << bsoncxx::to_json(maybeResult->view())<<std::endl;
    }
    // 查询匹配过滤器的多个文档,匹配同时满足以下条件为：
    // 1) 50 < i <= 100
    // 2) 按 i 降序排序
    // 3) 只返回 i 字段
    // 4) 只返回前 9 个值
    std::cout << "specify query filter,find() return values:" << std::endl;
    auto filter = document{} << "i" << open_document <<
        "$gt" << 50 << "$lte" << 100 << close_document << finalize;
    auto order = document{} << "i" << -1 << finalize;
    auto field = document{} << "_id" << 0 << "i" << 1 << finalize;
    mongocxx::options::find opts = mongocxx::options::find{};
    opts.sort(order.view()).projection(field.view()).limit(9);
    mongocxx::cursor cur = coll.find(filter.view(), opts);
    for (bsoncxx::document::view docView : cur)
    {
        std::cout << bsoncxx::to_json(docView) << "\n";
    }

    system("pause");
    return 0;
}
更新（Update）文档
为了更新集合中的文档，你可以使用集合的 update_one() 和 update_many() 方法。

更新方法返回一个 std::optional< mongocxx::result::update > 实例，其保存了更新操作修改文档数量的信息。

更新单个文档
为了更新最多单个文档，请使用 update_one() 方法。

下面代码将会更新符合筛选器 {"i":10} 的第一个文档并将其 i 的值设为 110：

collection.update_one(document{} << "i" << 10 << finalize,
document{} << "$set" << open_document <<
"i" << 110 << close_document << finalize);
更新多个文档
为了更新符合过滤器的所有文档，请使用 update_many() 方法。

下面例子将会将值小于 100 的 i 增加 100：

bsoncxx::stdx::optional<mongocxx::result::update> result =
collection.update_many(
document{} << "i" << open_document <<
"$lt" << 100 << close_document << finalize,
document{} << "$inc" << open_document <<
"i" << 100 << close_document << finalize);

if(result) {
std::cout << result->modified_count() << "\n";
}
删除文档
为了从集合中删除文档，你可以使用集合的 delete_one() 和 delete_many() 方法。

删除方法将会返回一个 std::optional< mongocxx::result::delete > 方法，其保存了删除文档的数量。

删除单个文档
为了删除最多一个符合过滤器的文档，请使用 delete_one() 方法。

例如，删除一个符合过滤器 {"i":110} 的文档：

collection.delete_one(document{} << "i" << 110 << finalize);
删除匹配过滤器的所有文档
为了删除匹配过滤器的所有文档，请使用 delete_many() 方法。

下面例子将删除所有 i 值大于 100 的文档：

bsoncxx::stdx::optional<mongocxx::result::delete_result> result =
collection.delete_many(
document{} << "i" << open_document <<
"$gte" << 100 << close_document << finalize);

if(result) {
std::cout << result->deleted_count() << "\n";
}
创建索引（Indexes）
为了给一个或多个字段创建索引，传递一个索引规则文档（index specification document）给一个 mongocxx::collection 实例的 create_index() 方法。一个索引键规则文档（index key specification document）包含索引的字段以及每个字段的索引类型：

{ "index1": "<type>", "index2": <type> }
对于升序索引类型，指定 <type> 为 1.
对于降序索引类型，指定 <type> 为 -1.
下面的示例代码在 i 字段上创建了一个升序索引：

auto index_specification = document{} << "i" << 1 << finalize;
collection.create_index(std::move(index_specification));
对应上面官方例程，编写代码如下。

/*  @file updatedelete.cpp
@brief 本代码演示了如何访问 mongodb 并更新和删除文档

1. 本代码应该在 insertquery 项目运行之后运行，否则会运行出错
2. 演示了单文档更新和多文档更新方法，并输出对应结果
3. 演示了单文档删除和多文档删除方法，并输出对应结果
4. 演示了创建索引并输出对应结果
   */

#include <cstdint>
#include <iostream>
#include <vector>
#include <bsoncxx/json.hpp>
#include <mongocxx/client.hpp>
#include <mongocxx/stdx.hpp>
#include <mongocxx/uri.hpp>
// 官方文档中缺少以下语句
#include <bsoncxx\builder\stream\document.hpp>       
#include <mongocxx\instance.hpp>

using bsoncxx::builder::stream::close_array;
using bsoncxx::builder::stream::close_document;
using bsoncxx::builder::stream::document;
using bsoncxx::builder::stream::finalize;
using bsoncxx::builder::stream::open_array;
using bsoncxx::builder::stream::open_document;

int main(int, char**)
{
mongocxx::instance instance{};      // This should be done only once.
// mongocxx::client client{ mongocxx::uri{} };  
// 上面代码等同于:
mongocxx::uri uri("mongodb://localhost:27017");
mongocxx::client client(uri);

    mongocxx::database db = client["mydb"];     // 访问数据库
    mongocxx::collection coll = db["test"];     // 访问集合

    // 最多更新一个文档
    mongocxx::stdx::optional<mongocxx::result::update> updateOneRes = coll.update_one(document{} << "i" << 10 << finalize,
        document{} << "$set" << open_document <<
        "i" << 110 << close_document << finalize);
    if (updateOneRes)
    {
        auto matchedCount = updateOneRes->matched_count();
        auto modifiedCount = updateOneRes->modified_count();
        std::cout << "update_one() method result:\n" <<
            "matched count: " << matchedCount << ",modified count: " << modifiedCount << std::endl;
        mongocxx::stdx::optional<bsoncxx::document::element> upId = updateOneRes->upserted_id();
        if (upId)
        {
            auto idStr = upId->get_oid().value.to_string();
            std::cout << "upserted id: " << idStr << std::endl;
        }
        else
            std::cout << "no document has been inserted." << std::endl;
    }
    // 更新多个文档
    mongocxx::stdx::optional<mongocxx::result::update> updateManyRes = coll.update_many(document{} << "i" << open_document <<
        "$lt" << 100 << close_document << finalize,
        document{} << "$inc" << open_document <<
        "i" << 100 << close_document << finalize);
    if (updateManyRes)
    {
        std::cout <<"update_many() method result:\nmodified count:" << updateManyRes->modified_count() << std::endl;
    }

    // 删除单个文档
    mongocxx::stdx::optional<mongocxx::result::delete_result> delOneRes = coll.delete_one(document{} << "i" << 110 << finalize);
    if (delOneRes)
    {
        std::cout << "delete_one() method result: " << delOneRes->deleted_count() << " document has been deleted." << std::endl;
    }
    // 删除多个文档
    mongocxx::stdx::optional<mongocxx::result::delete_result> delManyRes = coll.delete_many(
        document{} << "i" << open_document << "$gte" << 150 << close_document << finalize);
    if (delManyRes)
    {
        std::cout << "delete_many() method result: " << delManyRes->deleted_count() << " documents has been deleted." << std::endl;
    }

    // 按照 i 字段升序的方式建立索引
    auto index_specification = document{} << "i" << 1 << finalize;
    bsoncxx::document::value  val = coll.create_index(std::move(index_specification));
    std::cout << "create index result:" << std::endl;
    std::cout << bsoncxx::to_json(val.view()) << std::endl;

    system("pause");
    return 0;
}
示例代码的构建
对于以上译者给出的三个示例代码，将其内容粘贴然后分别保存到同一目录下的 access.cpp,insertquery.cpp 以及 updatedelete.cpp 文件中。该该文件夹中建立 CMakeLists.txt 文件在其中加入以下内容：

cmake_minimum_required(VERSION 3.11)
PROJECT (mongooperate)
# SET (SRC_LIST test.cpp)
find_package(libmongocxx REQUIRED)
find_package(Boost REQUIRED)

add_executable(access access.cpp)
target_include_directories(access
PRIVATE ${Boost_INCLUDE_DIRS}
)
target_include_directories(access
PRIVATE ${LIBMONGOCXX_INCLUDE_DIRS}
)
target_link_libraries(access
PRIVATE ${LIBMONGOCXX_LIBRARIES}
)
target_compile_definitions(access
PRIVATE ${LIBMONGOCXX_DEFINITIONS}
)

add_executable(insertquery insertquery.cpp)
target_include_directories(insertquery
PRIVATE ${Boost_INCLUDE_DIRS}
)
target_include_directories(insertquery
PRIVATE ${LIBMONGOCXX_INCLUDE_DIRS}
)
target_link_libraries(insertquery
PRIVATE ${LIBMONGOCXX_LIBRARIES}
)
target_compile_definitions(insertquery
PRIVATE ${LIBMONGOCXX_DEFINITIONS}
)

add_executable(updatedelete updatedelete.cpp)
target_include_directories(updatedelete
PRIVATE ${Boost_INCLUDE_DIRS}
)
target_include_directories(updatedelete
PRIVATE ${LIBMONGOCXX_INCLUDE_DIRS}
)
target_link_libraries(updatedelete
PRIVATE ${LIBMONGOCXX_LIBRARIES}
)
target_compile_definitions(updatedelete
PRIVATE ${LIBMONGOCXX_DEFINITIONS}
)
最后在该文件夹下建立一个 build 文件夹，在 build 文件夹下执行以下命令:

cmake -G "Visual Studio 15 2017 Win64" -DCMAKE_PREFIX_PATH=D:\software4\mongo_c_driver\cppinstall_release -DBOOST_ROOT=D:\software3\boost_1_67_0 ..
注意：

你应该根据自己 mogo_c_driver 及 boost 安装的位置调整对应的参数
你应该根据自己所使用的编译器版本调整编译器参数
最后的 .. 参数不能省略
结束语
本文仅仅是对 mongocxx 中增删查改基本操作方法进行介绍。想了解更多关于 mongocxx 和 mongodb 的内容，可以查阅官方介绍或其他第三方的介绍。如果想了解 mogocxx 的安装方法可以参考：

作者：刘亚彬92
链接：https://www.jianshu.com/p/0959d2221f43
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

dhh.dll文件在vcpkg文件夹下