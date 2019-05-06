// <script src="../../layuiadmin/util/myaes.js"></script>   
document.write("<script language=javascript src='../../layuiadmin/util/aes.js'></script>");
function encrypt(word,password){
	  password += ''
        var len = password.length
	    if(len<16){
	    	for(var i=0; i<16-len; i++){
	    		password=password+' '
	    	}
	    }
	    //var key = CryptoJS.enc.Utf8.parse("abcdefgabcdefg12");
	    var key = CryptoJS.enc.Utf8.parse(password);
	    var srcs = CryptoJS.enc.Utf8.parse(word);
	    var encrypted = CryptoJS.AES.encrypt(srcs, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
	    return encrypted.toString();
	}
  

  function decrypt(word,password){
	  password += ''
	        var len = password.length
		    if(len<16){
		    	for(var i=0; i<16-len; i++){
		    		password=password+' '
		    	}
		    }	  
    var key = CryptoJS.enc.Utf8.parse(password);
    var decrypt = CryptoJS.AES.decrypt(word, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
    return CryptoJS.enc.Utf8.stringify(decrypt).toString();
}