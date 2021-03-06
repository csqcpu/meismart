/** layuiAdmin.std-v1.1.0 LPPL License By http://www.layui.com/admin/ */
;
layui.define(["table", "form", "rest"],
function(e) {
    var t = layui.$,
    rest = layui.rest,
    i = layui.table,
    admin = layui.admin;
    layui.form;
    i.render({
        elem: "#LAY-adlocation-manage",
        url: layui.setter.base + "../../rest/ad/getlocation",
        method: 'post',
        contentType: 'application/json',
        done:function(res){
        	if(res.perm.insert==null || res.perm.insert=='')
        	    t("[data-type='add']").addClass("layui-btn layuiadmin-btn-useradmin layui-btn-disabled").prop("disabled" , true);
        	if(res.perm.delete==null || res.perm.delete=='')
        		t("[data-type='batchdel']").addClass("layui-btn layuiadmin-btn-useradmin layui-btn-disabled").prop("disabled" , true);
        },
        cols: [[{
            type: "checkbox",
            fixed: "left"
        },
        {
            field: "location_id",
            title: "编号",
            minWidth: 100
        },
        {
            field: "stb_id",
            title: "盒子型号",
            minWidth: 100
        },
        {
            field: "name",
            title: "广告位名称",
        },
        {
            field: "type_id",
            title: "广告类型"
        },
        {
            field: "description",
            title: "说明",
            templet: "#videoTpl",
            width: 200
        },
        {
            field: "imageurl",
            title: "示意图片",
            width: 100
        },
        {
            field: "status",
            title: "审核状态",
            templet: "#buttonTpl",
            sort: !0
        },
        {
            field: "createdt",
            title: "创建时间",
            sort: !0
        },
        {
            field: "createuser",
            title: "创建用户",
            sort: !0
        },
        {
            field: "checkdt",
            title: "审核时间",
            templet: "#buttonTpl",
            minWidth: 80,
            align: "center",
            sort: !0
        },
        {
            field: "checkuser",
            title: "审核人",
            sort: !0
        },
        {
            title: "操作",
            minWidth: 350,
            align: "left",
            fixed: "right",
            toolbar: "#table-useradmin-webuser"
        }]],
        page: !0,
        limit: 30,
        height: "full-220",
        text: "对不起，加载出现异常！"
    }),
    i.on("tool(LAY-adlocation-manage)",
    function(e) {
        e.data;
        var jsondata = JSON.stringify(e.data) 
        if ("del" === e.event) layer.prompt({
            formType: 1,
            title: "敏感操作，请验证口令"
        },
        function(t, i) {
            layer.close(i),
            layer.confirm("真的删除行么",
            function(t) {
                e.del(),
                layer.close(t),
                rest.ajax({
                    type: 'POST',
                    url: layui.setter.base + "../../rest/ad/dellocation",
                    contentType: 'application/json',
                    data: function() {
                        return JSON.stringify(e.data);
                    } (),
                    dataType: "json",
                    success: function(data) {
                        if (data.code == 0) {
                            //登入成功的提示与跳转
                            layer.msg('删除广告位成功', {
                                offset: '15px',
                                icon: 1,
                                time: 1000
                            },
                            function() {
                                layui.table.reload("LAY-adlocation-manage")
                                //location.href = '../index.html'; //后台主页
                            });
                        } else {
                            layer.msg('删除广告位失败:' + data.msg, {
                                offset: '15px',
                                icon: 1,
                                time: 1000
                            },
                            function() {
                                //  location.href = '../index.html'; //后台主页
                            });
                        }
                    },
                    error: function(e, t) {
                        layer.msg('删除广告位失败：', {
                            offset: '15px',
                            icon: 1,
                            time: 1000
                        },
                        function() {
                            //   location.href = '../index.html'; //后台主页
                        });
                    }
                })
            })
        });
        else if ("edit" === e.event) {
            t(e.tr);
            layer.open({
                type: 2,
                title: "编辑广告位",
                content: "../../views/ad/locationform.html",
                maxmin: !0,
                area: ["500px", "650px"],
                btn: ["确定", "取消"],
                yes: function(e, t) {
                    var l = window["layui-layer-iframe" + e],
                    r = "LAY-adlocation-front-submit",
                    n = t.find("iframe").contents().find("#" + r);
                    l.layui.form.on("submit(" + r + ")",
                    function(t) {
                        field = t.field;
                        i.reload("LAY-adlocation-front-submit"),
                        layer.close(e)
                    }),
                    n.trigger("click") 
                    rest.ajax({
                        type: 'POST',
                        url: layui.setter.base + "../../rest/ad/updatelocation",
                        contentType: 'application/json;charset=utf-8',
                        data: function() {
                            var dataArray = new Array() 
                            dataArray[0] = field
                            var output = {}
                            output['data'] = dataArray
                            return output
                        } (),
                        dataType: "json",
                        success: function(data) {
                            if (data.code == 0) {
                                //登入成功的提示与跳转
                                layer.msg('编辑广告位成功', {
                                    offset: '15px',
                                    icon: 1,
                                    time: 1000
                                },
                                function() {
                                    layui.table.reload("LAY-adlocation-manage")
                                });
                            } else {
                                layer.msg('编辑广告位失败:' + data.msg, {
                                    offset: '15px',
                                    icon: 1,
                                    time: 1000
                                },
                                function() {
                                    //  location.href = '../index.html'; //后台主页
                                });
                            }
                        },
                        error: function(e, t) {
                            layer.msg('编辑广告位失败：', {
                                offset: '15px',
                                icon: 1,
                                time: 1000
                            },
                            function() {
                                //   location.href = '../index.html'; //后台主页
                            });
                        }
                    })
                },
                success: function(layero, index) {
                    var body = layer.getChildFrame('body', index);
                    var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
                    iframeWin.document.getElementsByName('location_id')[0].value = e.data.location_id || ''
                    iframeWin.loadDepartmentName(e.data.stb_id)
                    //iframeWin.$("select[name='stb_id']").find('option:eq('+e.data.stb_id+')').attr('selected','selected');
                    iframeWin.$("select[name='type_id']").find('option[value='+e.data.type_id+']').attr('selected','selected');
                    iframeWin.document.getElementsByName('name')[0].value = e.data.name || ''
                    iframeWin.document.getElementsByName('imageurl')[0].value = e.data.imageurl || ''
                    iframeWin.document.getElementsByName('description')[0].value = e.data.description || ''
                    //iframeWin.document.getElementsByName('status')[0].value=e.data.status||''
                    //iframeWin.document.getElementsByName('account')[0].value=e.data.account||''
                    //body.find(".phone").val('12345678901');
                }
            })
        } else if ("play" === e.event) {
            t(e.tr);
            layer.open({
                type: 2,
                title: "广告预览",
                content: "../../views/ad/playadvideo.html",
                maxmin: !0,
                area: ["850px", "650px"],
                //area: ["500px", "450px"],
                btn: ["确定", "取消"],
                yes: function(e, t) {
                    layer.close(e)
                },
                success: function(layero, index) {
                    var body = layer.getChildFrame('body', index);
                    var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
                    if (e.data.type_id == 0) {
                        iframeWin.document.getElementsByName('video')[0].src = e.data.url || ''
                        iframeWin.document.getElementsByName('video')[0].play()
                    } else if (e.data.type_id == 1) {
                        iframeWin.document.getElementsByName('video')[0].poster = e.data.url || ''
                        iframeWin.document.getElementsByName('video')[0].play()
                        // 图片地址
                        var img_url = e.data.url;
                        // 创建对象
                        var img = new Image();
                        // 改变图片的src
                        img.src = img_url;
                        iframeWin.document.getElementsByName('video_widthheight')[0].value = img.width + "x" + img.height
                    }
                    //				    iframeWin.document.getElementsByName('corp')[0].value=e.data.corp||''
                    //				    iframeWin.document.getElementsByName('contact')[0].value=e.data.contact||''
                    //				    iframeWin.document.getElementsByName('mobile')[0].value=e.data.mobile||''
                    //				    iframeWin.document.getElementsByName('telephone')[0].value=e.data.telephone||''
                    //				    iframeWin.document.getElementsByName('addr')[0].value=e.data.addr||''
                    //				    iframeWin.document.getElementsByName('postcode')[0].value=e.data.postcode||''
                    //				    iframeWin.document.getElementsByName('account')[0].value=e.data.account||''
                    //body.find(".phone").val('12345678901');
                }
            })
        } else if ("submitcheck" === e.event) layer.confirm("确定提交审核么",
        function(t) {
            //e.del(), 
            layer.close(t),
            rest.ajax({
                type: 'POST',
                url: layui.setter.base + "../../rest/ad/submitchecklocation",
                contentType: 'application/json',
                data: function() {
                    var dataArray = new Array() 
                    dataArray[0] = e.data
                    var output = {}
                    output['data'] = dataArray
                    return output
                } (),
                dataType: "json",
                success: function(data) {
                    if (data.code == 0) {
                        //登入成功的提示与跳转
                        layer.msg('提交审核成功', {
                            offset: '15px',
                            icon: 1,
                            time: 1000
                        },
                        function() {
                            layui.table.reload("LAY-adlocation-manage")
                            //location.href = '../index.html'; //后台主页
                        });
                    } else {
                        layer.msg('提交审核失败:' + data.msg, {
                            offset: '15px',
                            icon: 1,
                            time: 1000
                        },
                        function() {
                            //  location.href = '../index.html'; //后台主页
                        });
                    }
                },
                error: function(e, t) {
                    layer.msg('提交审核失败：', {
                        offset: '15px',
                        icon: 1,
                        time: 1000
                    },
                    function() {
                        //   location.href = '../index.html'; //后台主页
                    });
                }
            })
        });
        else if ("checkuserpass" === e.event) layer.confirm("确定审核通过么",
        function(t) {
            //e.del(), 
            layer.close(t),
            rest.ajax({
                type: 'POST',
                url: layui.setter.base + "../../rest/ad/checklocationpass",
                contentType: 'application/json',
                data: function() {
                    var dataArray = new Array() 
                    dataArray[0] = e.data
                    var output = {}
                    output['data'] = dataArray
                    return output
                } (),
                dataType: "json",
                success: function(data) {
                    if (data.code == 0) {
                        //登入成功的提示与跳转
                        layer.msg('审核通过成功', {
                            offset: '15px',
                            icon: 1,
                            time: 1000
                        },
                        function() {
                            layui.table.reload("LAY-adlocation-manage")
                            //location.href = '../index.html'; //后台主页
                        });
                    } else {
                        layer.msg('审核通过失败:' + data.msg, {
                            offset: '15px',
                            icon: 1,
                            time: 1000
                        },
                        function() {
                            //  location.href = '../index.html'; //后台主页
                        });
                    }
                },
                error: function(e, t) {
                    layer.msg('审核通过失败：', {
                        offset: '15px',
                        icon: 1,
                        time: 1000
                    },
                    function() {
                        //   location.href = '../index.html'; //后台主页
                    });
                }
            })
        });
        else if ("checkuserfail" === e.event) layer.confirm("确定审核不通过么",
        function(t) {
            //e.del(), 
            layer.close(t),
            rest.ajax({
                type: 'POST',
                url: layui.setter.base + "../../rest/ad/checklocationfail",
                contentType: 'application/json',
                data: function() {
                    var dataArray = new Array() 
                    dataArray[0] = e.data
                    var output = {}
                    output['data'] = dataArray
                    return output
                } (),
                dataType: "json",
                success: function(data) {
                    if (data.code == 0) {
                        //登入成功的提示与跳转
                        layer.msg('审核不通过成功', {
                            offset: '15px',
                            icon: 1,
                            time: 1000
                        },
                        function() {
                            layui.table.reload("LAY-adlocation-manage")
                            //location.href = '../index.html'; //后台主页
                        });
                    } else {
                        layer.msg('审核不通过失败:' + data.msg, {
                            offset: '15px',
                            icon: 1,
                            time: 1000
                        },
                        function() {
                            //  location.href = '../index.html'; //后台主页
                        });
                    }
                },
                error: function(e, t) {
                    layer.msg('审核不通过失败：', {
                        offset: '15px',
                        icon: 1,
                        time: 1000
                    },
                    function() {
                        //   location.href = '../index.html'; //后台主页
                    });
                }
            })
        })
    }),
    e("adlocation", {})
});