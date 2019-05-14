/** layuiAdmin.std-v1.1.0 LPPL License By http://www.layui.com/admin/ */ ;
layui.define(["rest","table", 'util',"form"], function(e) {
	$ = layui.jquery; 
	var t = layui.$,
	rest = layui.rest,
	util=layui.util,
	i = layui.table;
	layui.form;
	i.render({
		elem: "#LAY-aduser-manage",
		url: layui.setter.base + "../../rest/ad/getuser",
        method:'post',
//       where: function(){
//        	var output={}
//        	output['data']=[]
//        	req = formatReq(output)
//        	return req
//        }(),
//        parseData: function (res) {
// 		   var data = decrypt(res.data,layui.data(layui.setter.tableName).password)
// 		   data = JSON.parse(data)
// 		   res.data=data
// 		   return res
// 	    },
        contentType:'application/json',
        toolbar: '#test-table-toolbar-toolbarDemo',
		cols: [
			[{
				type: "checkbox",
				fixed: "left"
			},{
				field: "username",
				title: "用户名",
				minWidth: 60
			}, {
				field: "corp",
				title: "公司名称",
				minWidth: 100
			},	{
				field: "contact",
				title: "联系人"
			}, {
				field: "mobile",
				title: "联系手机"
			}, {
				field: "telephone",
				title: "联系电话"
			}, {
				field: "addr",
				title: "地址",
				width: 100
			}, {
				field: "postcode",
				title: "邮编"
			}, {				
				field: "account",
				title: "余额(单位:分)",
				sort: !0
			}, {
				field: "pay_id",
				title: "广告扣费方式",
				sort: !0
			}, {
				field: "status",
				title: "状态",
				templet: "#buttonTpl",
				minWidth: 80,
				align: "center",
				sort: true
			}, {
				field: "createuser",
				title: "创建人",
				sort: !0
			}, {
				field: "createdt",
				title: "创建时间",
				templet: function(d) {return util.toDateString(d.createdt); } ,				
				sort: !0
			}, {
				title: "操作",
				width: 280,
				align: "left",
				fixed: "right",
				toolbar: "#table-useradmin-webuser"
			}]
		],
		page: !0,
		limit: 30,
		height: "full-220",
		text: "对不起，加载出现异常！"
	}), i.on("tool(LAY-aduser-manage)", function(e) {
		e.data;
		var jsondata = JSON.stringify(e.data)
		if ("del" === e.event) layer.prompt({
			formType: 1,
			title: "敏感操作，请验证口令"
		}, function(t, i) {
	        if(t!=layui.data(layui.setter.tableName).password){
	          	layer.msg('密码错误！', {
	                  offset: '15px'
	                  ,icon: 1
	                  ,time: 1000
	                })
	          	return
	          }
	        layer.close(i);
			layer.confirm("真的删除行么", function(t) {
				e.del(), layer.close(t),
				rest.ajax({
		                type: 'POST',
		                url: layui.setter.base + "../../rest/ad/deluser",
		                contentType: 'application/json',
		                data: function(){
		                	var dataArray = new Array()
		                	dataArray[0]=e.data
		                	var output={}
		                	output['data']=dataArray
		                	return output
		                }(),
		                dataType: "json",
		                success: function(data) {
		                    if(data.code==0){
		                        //登入成功的提示与跳转
		                        layer.msg('删除用户成功', {
		                          offset: '15px'
		                          ,icon: 1
		                          ,time: 1000
		                        }, function(){
		                        	layui.table.reload("LAY-aduser-manage")
		                          //location.href = '../index.html'; //后台主页
		                        });
		                    }
		                    else{
		                        layer.msg('删除用户失败:'+data.msg, {
			                          offset: '15px'
			                          ,icon: 1
			                          ,time: 1000
			                        }, function(){
			                        //  location.href = '../index.html'; //后台主页
			                        });
		                    }
		                },
		                error: function(e, t) {
	                        layer.msg('删除用户失败：', {
		                          offset: '15px'
		                          ,icon: 1
		                          ,time: 1000
		                        }, function(){
		                       //   location.href = '../index.html'; //后台主页
		                        });
		                }
		            })
			})
		});
		else if ("submitcheck" === e.event) layer.confirm("确定提交审核么", function(t) {
				//e.del(), 
				layer.close(t),
				rest.ajax({
		                type: 'POST',
		                url: layui.setter.base + "../../rest/ad/submitcheckuser",
		                contentType: 'application/json',
		                data: function(){
		                	var dataArray = new Array()
		                	dataArray[0]=e.data
		                	var output={}
		                	output['data']=dataArray
		                	return output
		                }(),
		                dataType: "json",
		                success: function(data) {
		                    if(data.code==0){
		                        //登入成功的提示与跳转
		                        layer.msg('提交审核成功', {
		                          offset: '15px'
		                          ,icon: 1
		                          ,time: 1000
		                        }, function(){
		                        	layui.table.reload("LAY-aduser-manage")
		                          //location.href = '../index.html'; //后台主页
		                        });
		                    }
		                    else{
		                        layer.msg('提交审核失败:'+data.msg, {
			                          offset: '15px'
			                          ,icon: 1
			                          ,time: 1000
			                        }, function(){
			                        //  location.href = '../index.html'; //后台主页
			                        });
		                    }
		                },
		                error: function(e, t) {
	                        layer.msg('提交审核失败：', {
		                          offset: '15px'
		                          ,icon: 1
		                          ,time: 1000
		                        }, function(){
		                       //   location.href = '../index.html'; //后台主页
		                        });
		                }
		            })
		});
		else if ("checkuserpass" === e.event) layer.confirm("确定审核通过么", function(t) {
			//e.del(), 
			layer.close(t),
			rest.ajax({
	                type: 'POST',
	                url: layui.setter.base + "../../rest/ad/checkuserpass",
	                contentType: 'application/json',
	                data: function(){
	                	var dataArray = new Array()
	                	dataArray[0]=e.data
	                	var output={}
	                	output['data']=dataArray
	                	return output
	                }(),
	                dataType: "json",
	                success: function(data) {
	                    if(data.code==0){
	                        //登入成功的提示与跳转
	                        layer.msg('审核通过成功', {
	                          offset: '15px'
	                          ,icon: 1
	                          ,time: 1000
	                        }, function(){
	                        	layui.table.reload("LAY-aduser-manage")
	                          //location.href = '../index.html'; //后台主页
	                        });
	                    }
	                    else{
	                        layer.msg('审核通过失败:'+data.msg, {
		                          offset: '15px'
		                          ,icon: 1
		                          ,time: 1000
		                        }, function(){
		                        //  location.href = '../index.html'; //后台主页
		                        });
	                    }
	                },
	                error: function(e, t) {
                        layer.msg('审核通过失败：', {
	                          offset: '15px'
	                          ,icon: 1
	                          ,time: 1000
	                        }, function(){
	                       //   location.href = '../index.html'; //后台主页
	                        });
	                }
	            })
	});
		else if ("checkuserfail" === e.event) layer.confirm("确定审核不通过么", function(t) {
			//e.del(), 
			layer.close(t),
			rest.ajax({
	                type: 'POST',
	                url: layui.setter.base + "../../rest/ad/checkuserfail",
	                contentType: 'application/json',
	                data: function(){
	                	var dataArray = new Array()
	                	dataArray[0]=e.data
	                	var output={}
	                	output['data']=dataArray
	                	return output
	                }(),
	                dataType: "json",
	                success: function(data) {
	                    if(data.code==0){
	                        //登入成功的提示与跳转
	                        layer.msg('审核不通过成功', {
	                          offset: '15px'
	                          ,icon: 1
	                          ,time: 1000
	                        }, function(){
	                        	layui.table.reload("LAY-aduser-manage")
	                          //location.href = '../index.html'; //后台主页
	                        });
	                    }
	                    else{
	                        layer.msg('审核不通过失败:'+data.msg, {
		                          offset: '15px'
		                          ,icon: 1
		                          ,time: 1000
		                        }, function(){
		                        //  location.href = '../index.html'; //后台主页
		                        });
	                    }
	                },
	                error: function(e, t) {
                        layer.msg('审核不通过失败：', {
	                          offset: '15px'
	                          ,icon: 1
	                          ,time: 1000
	                        }, function(){
	                       //   location.href = '../index.html'; //后台主页
	                        });
	                }
	            })
	});
		else if ("edit" === e.event) {
			t(e.tr);
			layer.open({
				type: 2,
				title: "编辑用户",
				content: "../../views/ad/userform.html",
				maxmin: !0,
				area: ["500px", "650px"],
				btn: ["确定", "取消"],
				yes: function(e, t) {
					var l = window["layui-layer-iframe" + e],
						r = "LAY-aduser-front-submit",
						n = t.find("iframe").contents().find("#" + r);
					l.layui.form.on("submit(" + r + ")", function(t) {
						field=t.field;
						i.reload("LAY-aduser-front-submit"), layer.close(e)
					}), n.trigger("click")
					rest.ajax({
		                type: 'POST',
		                url: layui.setter.base + "../../rest/ad/updateuser",
		                contentType: 'application/json;charset=utf-8',
		                data: function(){
		                	var dataArray = new Array()
		                	dataArray[0]=field
		                	var output={}
		                	output['data']=dataArray
		                	return output
		                }(),
		                dataType: "json",
		                success: function(data) {
		                    if(data.code==0){
		                        //登入成功的提示与跳转
		                        layer.msg('编辑用户成功', {
		                          offset: '15px'
		                          ,icon: 1
		                          ,time: 1000
		                        }, function(){
		                        	layui.table.reload("LAY-aduser-manage")
		                        });
		                    }
		                    else{
		                        layer.msg('编辑用户失败:'+data.msg, {
			                          offset: '15px'
			                          ,icon: 1
			                          ,time: 1000
			                        }, function(){
			                        //  location.href = '../index.html'; //后台主页
			                        });
		                    }
		                },
		                error: function(e, t) {
	                        layer.msg('编辑用户失败：', {
		                          offset: '15px'
		                          ,icon: 1
		                          ,time: 1000
		                        }, function(){
		                       //   location.href = '../index.html'; //后台主页
		                        });
		                }
		            })
				},
				success: function(layero, index) {
				    var body = layer.getChildFrame('body', index);
				    var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
				    var responsedata = layui.data
				    iframeWin.document.getElementsByName('username')[0].value=e.data.username||''
				    //iframeWin.$("input[name='username'][type=text]").val(e.data.username||'');
				    iframeWin.document.getElementsByName('corp')[0].value=e.data.corp||''
				    iframeWin.document.getElementsByName('contact')[0].value=e.data.contact||''
				    iframeWin.document.getElementsByName('mobile')[0].value=e.data.mobile||''
				    iframeWin.document.getElementsByName('telephone')[0].value=e.data.telephone||''
				    iframeWin.document.getElementsByName('addr')[0].value=e.data.addr||''
				    iframeWin.document.getElementsByName('postcode')[0].value=e.data.postcode||''
				    iframeWin.document.getElementsByName('account')[0].value=e.data.account||''
				    //$("input[name='sex'][type=radio][value='"+e.data.sex+"']",iframeWin.document).attr("checked",true);
				    iframeWin.$("input[name='sex'][type=radio][value='"+e.data.sex+"']").attr("checked",true);
					}
			})
		}
	}), i.render({
		elem: "#LAY-aduser-back-manage",
		url: layui.setter.base + "json/useradmin/mangadmin.js",
		cols: [
			[{
				type: "checkbox",
				fixed: "left"
			}, {
				field: "id",
				width: 80,
				title: "ID",
				sort: !0
			}, {
				field: "loginname",
				title: "登录名"
			}, {
				field: "telphone",
				title: "手机"
			}, {
				field: "email",
				title: "邮箱"
			}, {
				field: "role",
				title: "角色"
			}, {
				field: "jointime",
				title: "加入时间",
				sort: !0
			}, {
				field: "check",
				title: "审核状态",
				templet: "#buttonTpl",
				minWidth: 80,
				align: "center"
			}, {
				title: "操作",
				width: 150,
				align: "center",
				fixed: "right",
				toolbar: "#table-useradmin-admin"
			}]
		],
		text: "对不起，加载出现异常！"
	}), i.on("tool(LAY-aduser-back-manage)", function(e) {
		e.data;
		if ("del" === e.event) layer.prompt({
			formType: 1,
			title: "敏感操作，请验证口令"
		}, function(t, i) {
			layer.close(i), layer.confirm("确定删除此管理员？", function(t) {
				console.log(e), e.del(), layer.close(t)
			})
		});
		else if ("edit" === e.event) {
			t(e.tr);
			layer.open({
				type: 2,
				title: "编辑管理员",
				content: "../../views/user/administrators/adminform.html",
				area: ["420px", "420px"],
				btn: ["确定", "取消"],
				yes: function(e, t) {
					var l = window["layui-layer-iframe" + e],
						r = "LAY-aduser-back-submit",
						n = t.find("iframe").contents().find("#" + r);
					l.layui.form.on("submit(" + r + ")", function(t) {
						t.field;
						i.reload("LAY-aduser-front-submit"), layer.close(e)
					}), n.trigger("click")
				},
				success: function(e, t) {}
			})
		}
	}), i.render({
		elem: "#LAY-aduser-back-role",
		url: layui.setter.base + "json/useradmin/role.js",
		cols: [
			[{
				type: "checkbox",
				fixed: "left"
			}, {
				field: "id",
				width: 80,
				title: "ID",
				sort: !0
			}, {
				field: "rolename",
				title: "角色名"
			}, {
				field: "limits",
				title: "拥有权限"
			}, {
				field: "descr",
				title: "具体描述"
			}, {
				title: "操作",
				width: 150,
				align: "center",
				fixed: "right",
				toolbar: "#table-useradmin-admin"
			}]
		],
		text: "对不起，加载出现异常！"
	}), i.on("tool(LAY-aduser-back-role)", function(e) {
		e.data;
		if ("del" === e.event) layer.confirm("确定删除此角色？", function(t) {
			e.del(), layer.close(t)
		});
		else if ("edit" === e.event) {
			t(e.tr);
			layer.open({
				type: 2,
				title: "编辑角色",
				content: "../../views/user/administrators/roleform.html",
				area: ["500px", "480px"],
				btn: ["确定", "取消"],
				yes: function(e, t) {
					var l = window["layui-layer-iframe" + e],
						r = t.find("iframe").contents().find("#LAY-aduser-role-submit");
					l.layui.form.on("submit(LAY-aduser-role-submit)", function(t) {
						t.field;
						i.reload("LAY-aduser-back-role"), layer.close(e)
					}), r.trigger("click")
				},
				success: function(e, t) {}
			})
		}
	}), e("aduser", {})
});