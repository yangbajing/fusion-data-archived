# API服务设计

- 目录（category）。用以区分资源分类，目录可以有层级。
- 资源（resource）。用以唯一确定一个资源数据，资源需要包含在某一目录层级内。

API服务基于 HTTP 协议提供 Restful 形式的接口，使用JSON格式进行数据序列化。

**资源获取URI：**

```
GET /category/[<目录1>/<目录2>/....]/resource/<资源ID>
```

**分页查询：**

```
GET /category/[<目录1>/<目录2>/....]?page=<page>&size=<size>&pagerId=<size>
```

或

```
PUT /category/[<目录1>/<目录2>/....]/_search

{
  "page": 1,
  "size": 30,
  "pagerId": <pagerId>
}
```
