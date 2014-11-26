/*
 * jQuery mcdtree Plugin
 * version: 1.00 (14-12-2009)
 * @requires jQuery v1.2.6 or later
 */


//实现firefox兼容IE的一些方法
if(window.HTMLElement) {
    HTMLElement.prototype.__defineSetter__("outerHTML",function(sHTML){
        var r=this.ownerDocument.createRange();
        r.setStartBefore(this);
        var df=r.createContextualFragment(sHTML);
        this.parentNode.replaceChild(df,this);
        return sHTML;
        });

    HTMLElement.prototype.__defineGetter__("outerHTML",function(){
    	var attr;
        var attrs=this.attributes;
        var str="<"+this.tagName.toLowerCase();
        for(var i=0;i<attrs.length;i++){
            attr=attrs[i];
            if(attr.specified)
                str+=" "+attr.name+'="'+attr.value+'"';
            }
        if(!this.canHaveChildren)
            return str+">";
        return str+">"+this.innerHTML+"</"+this.tagName.toLowerCase()+">";
        });
        
	HTMLElement.prototype.__defineGetter__("canHaveChildren",function(){
		switch(this.tagName.toLowerCase()){
            case "area":
            case "base":
         case "basefont":
            case "col":
            case "frame":
            case "hr":
            case "img":
            case "br":
            case "input":
            case "isindex":
            case "link":
            case "meta":
            case "param":
            return false;
        }
        return true;
     });
}

//定义变量
var html = "";
var i ;
////////////////////////////////////////////////////
//定义图形变量
var img_plus_first = ROOTPath+"/platform/images/treeimg/plus.gif" //第一个节点
var img_minus_first = ROOTPath+"/platform/images/treeimg/minus.gif";

var img_plus = ROOTPath+"/platform/images/treeimg/plus.gif" ;//图形＋
var img_plus_end = ROOTPath+"/platform/images/treeimg/endplus.gif"; //未展开的最后一个节点

var img_minus = ROOTPath+"/platform/images/treeimg/minus.gif" ;//图形－
var img_node = ROOTPath+"/platform/images/treeimg/node.gif" ;//图形|-
var img_end = ROOTPath+"/platform/images/treeimg/end.gif" ;//图形|_
var img_minus_end = ROOTPath+"/platform/images/treeimg/endminus.gif" //有子节点，最后一个节点
var img_null = ROOTPath+"/platform/images/treeimg/node.gif";

var img_back = ROOTPath+"/platform/images/treeimg/back.gif" ;//背景图形|(一条线）
var img_hide = "" ; //打开时的图片
var img_hide = "" ; //并合时的图片
var show_imgs = new Array() ;
var hide_imgs = new Array() ;
var node_select = "#7AC6E9" ; //选中节点要显示的背景色
var node_no_select = "#ffffff" ; //未选中节点要显示的背景色

var img_show = ROOTPath+"/platform/images/treeimg/DefaultFolderOpen.gif";
var img_hide = ROOTPath+"/platform/images/treeimg/DefaultFolderClosed.gif";
var img_hide_first = ROOTPath+"/platform/images/treeimg/DefaultFolderClosed.gif";

var img_leaf = ROOTPath+"/platform/images/treeimg/DefaultLeaf.gif";

var img_selected = ROOTPath+"/platform/images/treeimg/leafSSearch.gif"; //选定的图

///////////////////////////////////////////////////
//
var se = "" ; //排序数
var curlen = 0; //上一节点sequence的长度
var prelen = 0; //本节点sequence的长度
var menu_id = "" ; //子节点TR的ID
var img_id = "" ; //本节点的IMG的ID
//var target = " target=_blank " ;
var target = "" ;
var history_id = "" ;

var isSupportMultiSelected = false; //支持多选
var isMenuMode=false;//true - 菜单模式  false - 选择模式
var isTreeMode=false;// true - click on branch cause expand; false - click is click.
var initIds = ""; //初如化IDS
var waitString = "正在打开...";
var aNodeObj = new Object; //nodeObj["xx"].status: "undefined":未打开过 1:打开状态 0 : 关闭状态

var	curNode = new aNode(); //加号对象
var oldNode = new aNode();

var curTxtNode = new aTxtNode();
var oldTxtNode = new aTxtNode();
var selectedDirIds = new Object();
var needTop = true;//是否需要虚拟顶点

function aNodeInfo(){
	this.status = "undefined";
	this.img_hide = "";
	this.img_show = "";
	this.img_plus = ""; //未展开的小图标
	this.img_minus = ""; //已展开的小图标
}
function aNode(){
	this.html = "";
	this.isLastNode = getLastNode;
	this.isFirstNode = getFirstNode;
	this.node = null;
	this.img = getNodeImg;
	this.clear = clearaNode;
}
function getFirstNode(){
	if(curNode.node){
		var node = curNode.node;
		var firstNode = node.siblings()[0];
		if (firstNode){
			if (node.attr("id") == firstNode.getAttribute("id")) return true;
			else return false;
		}else{
			return false;
		}
	}else{
		return false;
	}
}
function getLastNode(){
	if(curNode.node){
		var node = curNode.node;
		var lastNodeIndex = node.siblings().size();
		var lastNode = node.siblings()[lastNodeIndex-1];
		if (lastNode){
			if (node.attr("id") == lastNode.getAttribute("id")) return true;
			else return false;
		}else{ //第一层
			return false;
		}
	}else{
		return false;
	}
}
function getNodeImg(){
	var node = curNode.node;
	var img = "";
	if (node) {
		if (curNode.isLastNode()){
			if (curNode.html == "") {//有子节点，不是最后一个节点
				img = img_end;
			}else{
				img = img_minus_end;
			}
		}else{
			if (curNode.html == "") {//无子节点，不是最后一个节点
				img = img_node;
			}else{
				img = img_minus;
			}
		}
		if (curNode.isFirstNode()) {//是第一个节点
			if (curNode.html == "") {//无子节点，不是最后一个节点
				img = img_null;
			}else{
				img = img_minus_first;
			}
		}
		return img;
	}else{
		return img_node;
	}
}
function clearaNode(){
	curNode.html = "";
	curNode.node = null;
}
function aTxtNode(){
	this.selectedColor = '#7AC6E9';
	this.status = 0; //默认未选定
	this.setColor = setColor;
	this.select = txtNodeSelected;
	this.unSelect = txtNodeNonSelected;
	this.isSelected = isTxtNodeSelected;
	this.isEmpty = isTxtNodeEmpty;
	this.XMLNode = null;
	this.HTMLNode = null;
	this.nodeFlag = "";
}
function setColor(){
	if (this.status == 0) {//未选定
		var color = this.selectedColor;
		this.status = 1;
	}else{
		var color = '' ;
		this.status = 0;
	}
	eval("txt"+this.nodeFlag+".style.background = '" + color +"'");
}
function txtNodeSelected(){
	var imgHtml = '<img  align="absbottom"  id="nodeImg'+curTxtNode.nodeFlag + '" src=' + img_selected + ' valign=baseline align=absbottom border=0>';
	this.HTMLNode.innerHTML += imgHtml ;
	this.XMLNode.attr("initImg","yes");
	var id = this.XMLNode.attr("id");
	selectedDirIds[id] = 1;
}
function txtNodeNonSelected(){
	//为了防止未选定的时候再次选择
	try	{
		eval("var img = nodeImg"+this.nodeFlag);
		img.outerHTML = "";
		this.XMLNode.removeAttr("initImg");
		var id = this.XMLNode.attr("id");
		selectedDirIds[id] = 0;
	}catch(e){
	}
}
function isTxtNodeSelected(){
	if (this.XMLNode.attr("initImg") == null) return false;
	else return true;
}
function isTxtNodeEmpty(){
	return this.nodeFlag == "" ? 1 : 0;
}

function setMenuMode(t){
	isMenuMode = t;
}
function setTreeMode(t){
	isTreeMode = t;
}
function setInitIds(ids){
	initIds = ids;
}
// flag：是否需要展开
// hasRoot：是否需要虚拟根节点
function init(flag,hasRoot){
	if (hasRoot){
		needTop = hasRoot;
	}
	html += ' <table style="position:block;" border="0" cellspacing="0" cellpadding="0" >'+"\r\n" ;
	if (needTop){
		html += '<tr align="left">'+"\r\n" ;
		html += '  <td width="2%">'+"\r\n" ;
		html += '    <img  align="absbottom" border="0" src="'+ROOTPath+'/platform/images/treeimg/zb_topic_17.gif">' ;
		html += '  </td>'+"\r\n" ;
		html += '  <td style="cursor:hand" onclick=title_click("0","全部","","1")>&nbsp;全部'+"\r\n" ;
		html += '  </td>'+"\r\n" ;
		html += '</tr>'+"\r\n" ;
	}
	var num = 1;
	var nodeFlag = "1";
	var nodes = $("#xmlDoc").find("root").find("item1");
	if (nodes.size()>0){
		//初始化IDs
		if (initIds != ""){
			var aIds = initIds.split(",");
			for (var i=0;i<aIds.length;i++){
				selectedDirIds[aIds[i]] = 1;
			}
		}
		for(var i=0;i<nodes.size();i++){
			var isFirst = (i==0)? true : false;
			var isLast  = (i==nodes.size()-1)? true : false;
			html += getHTML(nodes[i],nodeFlag,num++,isLast,isFirst);
		}
	}
	html +="</table>" ;
	document.getElementById("TreeRoot").outerHTML =  html;

	//add by jeff
	for(var i=1;i<=nodes.length;i++){
		getSon(nodeFlag+"_"+i,nodes[i-1].getAttribute("id"));
	}
	if (flag != undefined && flag != null) openAll();
	//end add 
}
//node: xml的节点
//level: 第几层的表示法 例：第１层 1, 第１层下的第２层 1_2
//num: 第n层的第几个
//第１层下的第２层的第３个节点：完整的表示：　“menu1_2_3”
//相应的小图标的表示： “img1_2_3”
//上面的节点，他的未展开的节点的表示：　“menu1_2_3wait”

function getHTML(node,nodeFlag,num,isLast,isFirst){
	///////////////////////////////////////////////////////
	//定义本节点TR
	var s = "";
	var imgHtml = "";
	var nodeFlag = nodeFlag+"_"+num;
	var menu_id = "menu"+nodeFlag;
	var img_id  = "img"+nodeFlag;
	var wait_id = "wait"+nodeFlag;
	var title_id = "title"+nodeFlag;
	var id      = node.getAttribute("id");
	var url     = node.getAttribute("url");
	var tip     = node.getAttribute("tip");
	if(tip!=null&&tip!=""){
		tip="title=\""+tip+"\"";
	}else{
		tip="";
	}
	url = url?url:"#";
	//仅处理目录ＩＤｓ初始化
	var tmpIds = "," + initIds + ",";
	if (tmpIds.indexOf(","+id+",") != -1) {//默认选定
		imgHtml = '<img  align="absbottom"  id="nodeImg'+nodeFlag + '" src=' + img_selected + ' valign=baseline align=absbottom border=0>';
		node.setAttribute("initImg","yes");
	}
	
	var title   = '<span id=txt'+nodeFlag+' >'+node.getAttribute("value")+'</span>'+ imgHtml;
	
	if (isFirst){
		var img_plus_temp = img_plus_first;
		var img_hide_temp = img_hide_first;
	}else{
		var img_plus_temp = img_plus;
		var img_hide_temp = img_hide;
	}
	//Leaf mode : no children , use node img replacing plus one. liuhui 2003/11/4
	if(!node.hasChildNodes()){
		img_plus_temp=img_node;
	}	
	if (isLast){
		var back_img = "";
		var img_plus_temp = img_plus_end;
		if(!node.hasChildNodes()){
			img_plus_temp=img_end;
		}			
	}else{
		var	back_img = "background=\""+ROOTPath+"/platform/images/treeimg/back.gif\"";
	}
	
	//Image define : allow user change every node's img as wish. liuhui 2003/11/5
	if(node.getAttribute("img")!=null){
		img_hide_temp=node.getAttribute("img");	
	}else if(!node.hasChildNodes()){
		img_hide_temp=img_leaf;	
	}
	//end 
	
	aNodeObj[nodeFlag] = new aNodeInfo();
	aNodeObj[nodeFlag].img_hide = img_hide_temp;
	aNodeObj[nodeFlag].img_plus = img_plus_temp;
	
	//alert(back_img)
	var img_title = "<img  align='absbottom'  id="+title_id+" border=0 src='"+img_hide_temp+"' width=16 height=16>" ;

	s += '<tr align="left" id="'+menu_id+'"'+tip+'>'+"\r\n" ;
	if (isFirst){
		s += "  <td width=\"2%\" style=\"cursor:hand\" onmouseup=turnit('"+nodeFlag+"','"+id+"')  >"+"\r\n" ;
	}else{
		s += "  <td width=\"2%\" style=\"cursor:hand\" onmouseup=turnit('"+nodeFlag+"','"+id+"')  '"+back_img+"' >"+"\r\n" ;
	}
	s += '    <img align="absbottom" id="'+img_id+'" border="0" src="'+img_plus_temp+'">' ;
	s += '  </td>'+"\r\n" ;
	s += '  <td nowrap style="cursor:hand;" id="node' + nodeFlag + ((url=='#'&&isTreeMode)?'" onclick=turnit("':'" onclick=turnit_node("')+nodeFlag+'","'+id+'")>'+img_title+"&nbsp;"+title ;//Branch function : click on branch will cause not nothing but expanding. liuhui 2003/12/21
	s += '  </td>'+"\r\n" ;
	s += '</tr>'+"\r\n" ;
	
	//////////////////////////////////////////////////////
	//定义子节点TR头	//Tip function. liuhui 2002/11/14
	s += '<tr '+tip+' id="'+wait_id+'" style="display:none">'+"\r\n" ;
	s += '  <td '+back_img+' ></td>'+"\r\n" ;
	s += '  <td valign=middle nowrap>'+waitString+"</td>"+"\r\n" ;
	s += '</tr>';
	return s ;
}
var sons = new Array();
var n = 0;
var ids = "0";
var names = "";
function getSon(nodeFlag,id){
	var node = locateNode(id);
	if (node.children()>0&&node.name!="user"){
		sons[n++] = nodeFlag+" "+id;
		var nodes = node.children();
		for(var i=1;i<=nodes.size();i++){
			getSon(nodeFlag+"_"+i,nodes[i-1].attr("id"));
		}
	}
}
function locateNode(id){
	return $("#"+id);
}
function openAll(){
	for (var i=0; i<sons.length; i++){
		turnit(sons[i].split(" ")[0],sons[i].split(" ")[1]);
	}
}
function turnit_node(nodeFlag,id){
	var node = locateNode(id);
	if (node){
		eval("var obj = node" + nodeFlag);
		if(oldTxtNode.XMLNode == null){//第一次点击
			curTxtNode.HTMLNode = obj;
			curTxtNode.XMLNode = node;
			curTxtNode.nodeFlag = nodeFlag;
			curTxtNode.setColor();
			if (curTxtNode.isSelected()){//原来是选定的
				if(!isMenuMode){curTxtNode.unSelect();}//菜单模式下，继续选中
			}else{
				curTxtNode.select();
			}
			oldTxtNode = curTxtNode;
		}else{
			/*
			1.判断选中的是不是自已 old = cur　
			2.不是：cur.clear(),cur.nodeFlag = nodeFlag, cur.selected(),old.setColor()
				是 ：cur.NonSelected()
			3.是不是多选
			　不是：old.NonSelected()
			　 是 ：不操作
			4.    　
			*/
			oldTxtNode = curTxtNode;
			if(oldTxtNode.nodeFlag == nodeFlag) {//判断选中的是不是自已
				if (curTxtNode.isSelected()){//原来是选定的
					if(!isMenuMode){curTxtNode.unSelect();}//菜单模式下，继续选中
				}else{
					curTxtNode.select();
				}
			}else{
				oldTxtNode.setColor();
				curTxtNode = new aTxtNode();
				
				curTxtNode.nodeFlag = nodeFlag;
				curTxtNode.XMLNode = node;
				curTxtNode.HTMLNode = obj;
				curTxtNode.setColor();
				
				if(curTxtNode.isSelected()){//现在的是选定的
					curTxtNode.unSelect();
				}else{
					curTxtNode.select();
				}
			}
			
			if (isSupportMultiSelected) {//多选，看来多选还未实现啊--xuhang
				;
			}else{ //单选
				if (oldTxtNode != curTxtNode){
					oldTxtNode.unSelect();
				}
			}
		}
		//changed by jeff
		makeIDs(id,nodeFlag);
	}
}

function turnit(nodeFlag,id){
	var node = locateNode(id);
	if(!node.children()) return;
	eval("var wait_id = wait"+nodeFlag);
	eval("var menu_id = menu"+nodeFlag);
	eval("var img_id  = img"+nodeFlag);
	eval("var title_id = title"+nodeFlag);
	
	if(aNodeObj[nodeFlag].status == "undefined"){//未初始化
		initNode(nodeFlag,id);//取出下级节点树
		if(curNode.html != ""){
			wait_id.cells[1].innerHTML = curNode.html;
			wait_id.style.display = "";
			//nodeObj[nodeFlag] = 1;
			aNodeObj[nodeFlag].status = 1;
		}
		var img = curNode.img();
		//Img function.liuhui 2003/11/10	
		img_id.src = img;
		title_id.src = (curNode.node.attr("img")!=null)?(curNode.node.attr("img")):img_show;
		aNodeObj[nodeFlag].img_minus = img;
		aNodeObj[nodeFlag].img_show = (curNode.node.attr("img")!=null)?(curNode.node.attr("img")):img_show;
		//end			
	}else{
		if (aNodeObj[nodeFlag].status == 1) {//关闭
			wait_id.style.display = "none";
			img_id.src = aNodeObj[nodeFlag].img_plus;
			title_id.src = aNodeObj[nodeFlag].img_hide;
			aNodeObj[nodeFlag].status = 0;
		}else{ //打开
			wait_id.style.display = "";
			aNodeObj[nodeFlag].status = 1;
			img_id.src = aNodeObj[nodeFlag].img_minus;
			title_id.src = aNodeObj[nodeFlag].img_show;
		}
	}
	return;
}
function initNode(nodeFlag,id){
	var node = locateNode(id);
	if (node){
		oldNode = curNode;
		curNode.clear();
		curNode.node = node;
		var num = 1;
		var nodes = node.children();
		if (nodes.size()>0){
			var s = '      <table border="0" width="100%" cellpadding="0" cellspacing="">'+"\r\n" ;
			for(var i=0;i<nodes.length;i++)	{
				var isLast  = (i==nodes.length-1)? true : false;
				s += getHTML(nodes[i],nodeFlag,num++,isLast);
			}
			s +="</table>" ;
			curNode.html = s;
		}
	}else{
		alert("NULL")
	}
}
function makeIDs(id,nodeFlag){
	var node = locateNode(id);
	title_click(id,node.attr("value"),node.attr("url"),nodeFlag);
}
//-->