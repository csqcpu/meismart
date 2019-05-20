layui.define(['jquery'], function(exports){ 
    var $ = layui.jquery;
    var obj = {
       ajax: function (req) {
            $.ajax({
                type: req.type,                
                //url: req.url,
                url: req.url+'?time='+(new Date()).getTime(),
                contentType:req.contentType,
                dataType: req.dataType,
                headers: req.headers,
                data: function(){
                	
                	var JsonRequest = "object" == typeof req.data? req.data:JSON.parse(req.data)
                	var dataObject={}
                	dataObject['data']=JsonRequest.data
                	dataObject['timestamp']=layui.data(layui.setter.tableName).timestamp
                	var encryptdata=encrypt(JSON.stringify(dataObject),layui.data(layui.setter.tableName).password);
                	//var output={}
                	JsonRequest['token']=layui.data(layui.setter.tableName).token
                	JsonRequest['data']=encryptdata
                	return JSON.stringify(JsonRequest)
                }(),
                success: function(res){
                	if(res[layui.setter.response.statusName]==layui.setter.response.statusCode.logout){
                		parent.redirect()
                		return
                	}
                   if(res.data!=null){
                	   var data = decrypt(res.data,layui.data(layui.setter.tableName).password)
                	   data = JSON.parse(data)
             		   res.data=data
                   }
                   if(res.perm!=null){
          	          layui.data(layui.setter.tableName, {
       	                key:'perm',
       	                value:res.perm
       	              });
                   }
         		   if(res.timestamp!=null){
         	          layui.data(layui.setter.tableName, {
         	                key:'timestamp',
         	                value:res.timestamp
         	              });
         		   }
                   req.success(res)
                },
                error:function(e,t){
                	req.error(e,t)
                }
            });
        }
    };
    //输出接口
    exports('rest', obj);
});