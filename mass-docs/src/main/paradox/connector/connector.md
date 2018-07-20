# 扩展连接器

TODO 扩展数据连接器的方式，从 `Connector` trait 开始描述

数据连接器采用模块化、可扩展设计。对于 mass-connector 不支持的数据源，用户可以很方便的对系统进行扩展，支持自定义的数据源。

## `Connector`

`Connector` trait是数据连接器的基础接口，设置自定义数据连接器是需要实现这个接口。

@@snip [trait Connector](../../../../../mass-connector/src/main/scala/mass/connector/Connector.scala) { #Connector }
