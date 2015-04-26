<html>
<head>

<%
try{
	String user = session.getAttribute("LoggedInUserEmail").toString();

}
catch(Exception e)
{
	response.sendRedirect("../CollabEdit/");
	//out.println("The answer is " );
}
%>
	<link rel="stylesheet" type="text/css" href="css/create_file.css">
</head>
<body>
	<div id="top"></div>
	<div id="rest">
		<div  id="form">
			<form autocomplete="on" method = "post">
			<input type='button'	 id="loadCreateFile" value="Create New File">
			<input type="button" id="editOldFiles" value='Edit Old Files'>


				<div id="oldFiles">
					<div class="listOfFiles">List of old files</div>
					<ul id='oldFilesList'>
					</ul>	
				</div>	


				<div id='createFileOuterDiv'>
					<div id="create_file">Please add details for new File</div>
<!-- 					<input type="text" id='file' class="inputs file" name="nameoffile" placeholder="Name of File"> -->
 					<input id="file" name="nameoffile"  type="text" placeholder="Name of File" > 
					<br>
					<select name='select1' id='sel1'>
						<option selected value='NULL'>select the extension type</option>
						<option value='1'>.c</option>
						<option value='2'>.cpp</option>
						<option value='3'>.java</option>
						<option value='4'>.js</option>
						<option value='5'>.html</option>
						<option value='6'>.jsp</option>
	
						<option value='7'>.css</option>
						<option value='8'>.rb</option>
						<option value='9'>.vb</option>
						<option value='10'>.asp</option>
						<option value='11'>.pl</option>
						<option value='12'>.php</option>
					</select>
					<p>
						<select name='mailofmod' id='mail_mod'> 
						<option selected value='NULL'>Collaborate With</option>
						</select>
<!-- 						<input id="mail_mod" name="mailofmod" type="email" required placeholder="Share With (gmail ID only)" /> -->
						<input id="add_new" type="button" value="Create File" onClick = "clickFunction()">
					</p>
				</form>
			</div>
	</div>
	
	<div class="noticeForOldData">
		<div class="divForBlueColor">
			<div class="noticeText">

			</div>
		</div>
	</div>
</div>
	
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script>

//This ajax will get all the userMail IDs which have been registered with CollabEdit
	$.ajax({
		method: 'POST',
		url : '/CollabEdit/UsersDb',
		async: false
	}).done(function(data,status){
		var i =1;
		for(index in data)
		{
			$('#mail_mod').append("<option name='"+i+"'>"+data[index]+"</option>");
			i++;
		}
	});

//making OldFiles Div Visible
	$('#loadCreateFile').click(function(event) 
	{
		if($('#oldFiles').is(":visible"))
			$('#oldFiles').slideToggle(100);		
		$('#createFileOuterDiv').slideDown(500);
	});

	$('#editOldFiles').click(function(event) 
	{
		if($('#createFileOuterDiv').is(":visible"))
			$('#createFileOuterDiv').slideToggle(100);
		setTimeout(function()
		{	
			$('#oldFiles').slideDown(500);
		},200);
	});


//Getting the names of the old files, which are either created or shared with the logged In user
	$.ajax({
		url: '/CollabEdit/getPrevData',
		type: 'POST',
	})
	.done(function(data,status) {
		
		var fileNames=[],index=0;
		for(var i in data)
		{
			
			fileNames[index++]= data[i].substring(0,data[i].length-36);
			console.log("Data:    ",fileNames[index-1]);
			$('#oldFilesList').append("<li onclick=executeFunction('"+fileNames[index-1]+"') class='filename' id = '"+fileNames[index-1]+"'> <a href='editor.jsp'>" +fileNames[index-1].slice(0,fileNames[index-1].length-1)+" </a></li><br>");
		}
	});

//This is the click function on the Old Files
	function executeFunction(i){
		$.ajax({
			url: '/CollabEdit/SetCurrFile',
			type: 'POST',
			data: {fileName: i}
		})
		.done(function(data,status) {
			console.log("Session FILENAME set");
		});

	}

	//When createFile Button is clicked
	function clickFunction()
	{
		if(document.getElementById('file').value===''||document.getElementById('sel1').value==='NULL'||document.getElementById('mail_mod').value==='NULL')
		{
			$('.noticeText').html("");
			$('.noticeText').append("<div class='errorData'>Fill all the details </div>");
			$('.noticeText').append("<br><input type='button' value='Back' class='goBack'>");
			$('.noticeForOldData').slideToggle(500);
			$('.goBack').click(function(event)
			{
				$('.noticeForOldData').slideToggle(500);
			});
		
		}
		else
		{
			var option = document.getElementById("sel1").value;
			var mail_mod = document.getElementById('mail_mod').value;
			var file = document.getElementById('file').value.trim();
			var regEx = /\,|\s|\W/g;
			
			//Validating the FileName based on the constraints
			
			if(file.match(regEx)||file.length>11||file.length<4)
			{
				$('.noticeText').html("");
				$('.noticeText').append("<div class='errorData'><li>File name should not consist of any Spaces, Comma, or any Special Character </li><li> Length must be between 4 and 12 characters</li></div>");
				$('.noticeText').append("<br><input type='button' value='Back' class='goBack'>");
				$('.noticeForOldData').slideToggle(500);
				$('.goBack').click(function(event)
				{
					$('.noticeForOldData').slideToggle(500);
				});
			}
			
			else {
			
				//After validation, ajax request is sent to the server
			$.ajax({
						method : "POST",
						url : "/CollabEdit/Collaborate",
						data : {
							nameoffile 	: document.getElementById('file').value,
							select1 	: document.getElementById("sel1").value,
					  		mailofmod 	: document.getElementById('mail_mod').value
						},
						error : function(data)
						{
							//When data is not sent to  the server
							
							$('.noticeText').html("");
							$('.noticeText').append("<div class='errorData'>Can not Connect to Server, Try again</div>");
							$('.noticeText').append("<br><input type='button' value='Back' class='goBack'>");
							$('.noticeForOldData').slideToggle(500);
							$('.goBack').click(function(event)
							{
								$('.noticeForOldData').slideToggle(500);
							});
						}
					}).done(function(data, status) 
						{
							if (data['response'] == 'success') 
							{	
								window.location.assign("editor.jsp");
							} 
							else 
							{
								
								//File with the same exists 							
								$('.noticeText').html("");
								$('.noticeText').append("<div class='errorData'>File with the same name, already Exists</div>");
								$('.noticeText').append("<br><input type='button' value='Back' class='goBack'>");
								$('.noticeForOldData').slideToggle(500);
								$('.goBack').click(function(event)
								{
									$('.noticeForOldData').slideToggle(500);
								});			
							}
				});
			}
	}
	}
</script>
</body>
</html>
