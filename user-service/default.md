# OpenAPI definition


**简介**:OpenAPI definition


**HOST**:http://localhost:28080


**联系人**:


**Version**:v0


**接口路径**:/v3/api-docs/default


[TOC]






# 用户模块


## 注册


**接口地址**:`/user`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "username": "",
  "password": "",
  "nickname": "",
  "email": "",
  "phone": "",
  "avatar": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|registerDTO|RegisterDTO|body|true|RegisterDTO|RegisterDTO|
|&emsp;&emsp;username|用户名||true|string||
|&emsp;&emsp;password|密码||true|string||
|&emsp;&emsp;nickname|昵称||false|string||
|&emsp;&emsp;email|邮箱||false|string||
|&emsp;&emsp;phone|手机号||false|string||
|&emsp;&emsp;avatar|头像||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|message|信息|string||
|data|数据|object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 更新用户


**接口地址**:`/user`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>更新本人用户信息</p>



**请求示例**:


```javascript
{
  "password": "",
  "nickname": "",
  "email": "",
  "phone": "",
  "avatar": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|updateUserDTO|UpdateUserDTO|body|true|UpdateUserDTO|UpdateUserDTO|
|&emsp;&emsp;password|密码||false|string||
|&emsp;&emsp;nickname|昵称||false|string||
|&emsp;&emsp;email|邮箱||false|string||
|&emsp;&emsp;phone|手机号||false|string||
|&emsp;&emsp;avatar|头像||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|message|信息|string||
|data|数据|object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 删除用户


**接口地址**:`/user/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据ID删除用户</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseVoid|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|message|信息|string||
|data|数据|object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 登录


**接口地址**:`/user/login`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "username": "",
  "password": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|loginDTO|LoginDTO|body|true|LoginDTO|LoginDTO|
|&emsp;&emsp;username|用户名||true|string||
|&emsp;&emsp;password|密码||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseString|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|message|信息|string||
|data|数据|string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


## 获取信息


**接口地址**:`/user/me`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取本人用户信息</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseUserVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|message|信息|string||
|data||UserVO|UserVO|
|&emsp;&emsp;id|用户ID|integer(int64)||
|&emsp;&emsp;username|用户名|string||
|&emsp;&emsp;nickname|昵称|string||
|&emsp;&emsp;email|邮箱|string||
|&emsp;&emsp;phone|手机号|string||
|&emsp;&emsp;createdAt|注册时间|string(date-time)||
|&emsp;&emsp;updatedAt|更新时间|string(date-time)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"username": "",
		"nickname": "",
		"email": "",
		"phone": "",
		"createdAt": "",
		"updatedAt": ""
	}
}
```


## 查询用户


**接口地址**:`/user/query`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据条件查询用户</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|username|用户名|query|false|string||
|nickname|昵称|query|false|string||
|email|邮箱|query|false|string||
|phone|手机号|query|false|string||
|earliestCreatedAt|最早注册时间|query|false|string||
|lastCreatedAt|最晚注册时间|query|false|string||
|pageNum|页码|query|false|string||
|pageSize|每页大小|query|false|string||
|orderBy|排序字段|query|false|string||
|isAsc|是否升序|query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseUserPageVO|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|message|信息|string||
|data||UserPageVO|UserPageVO|
|&emsp;&emsp;pages|页数|integer(int64)||
|&emsp;&emsp;total|总数据量|integer(int64)||
|&emsp;&emsp;users|用户信息列表|array|UserVO|
|&emsp;&emsp;&emsp;&emsp;id|用户ID|integer||
|&emsp;&emsp;&emsp;&emsp;username|用户名|string||
|&emsp;&emsp;&emsp;&emsp;nickname|昵称|string||
|&emsp;&emsp;&emsp;&emsp;email|邮箱|string||
|&emsp;&emsp;&emsp;&emsp;phone|手机号|string||
|&emsp;&emsp;&emsp;&emsp;createdAt|注册时间|string||
|&emsp;&emsp;&emsp;&emsp;updatedAt|更新时间|string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"pages": 0,
		"total": 0,
		"users": [
			{
				"id": 0,
				"username": "",
				"nickname": "",
				"email": "",
				"phone": "",
				"createdAt": "",
				"updatedAt": ""
			}
		]
	}
}
```