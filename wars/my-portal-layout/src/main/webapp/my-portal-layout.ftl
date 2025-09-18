<!DOCTYPE html>
<html>
<head>
	<title>Corporate Portal</title>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<style>
		header { background: #003366; color: white; padding: 1rem; }
		nav ul { display: flex; list-style: none; gap: 2rem; }
		nav a { color: white; text-decoration: none; }
		nav a:hover { text-decoration: underline; }
		.layout-container { display: flex; }
		.sidebar { width: 250px; background: #f4f4f4; padding: 1rem; }
		.main-content { flex: 1; padding: 1rem; }
	</style>
	<link href="/o/my-react-spa/assets/index.css" rel="stylesheet">
</head>
<body class="">

<#if user??>
	<script>
		window.TEST_USER_DATA = {
			userId: ${user.getUserId()},
			screenName: "${user.getScreenName()?js_string}",
			firstName: "${user.getFirstName()?js_string}",
			lastName: "${user.getLastName()?js_string}",
			emailAddress: "${user.getEmailAddress()?js_string}"
		};
		console.log("User data from the Liferay platform :", window.TEST_USER_DATA);
	</script>
</#if>

<header>
	<h1>My Corporate Portal</h1>
	<nav>
		<ul>
			<li><a href="/web/my-portal">Home</a></li>
			<li><a href="/web/my-portal/news">News</a></li>
			<li><a href="/web/my-portal/docs">Documents</a></li>
			<li><a href="/web/my-portal/profile">Profile</a></li>
		</ul>
	</nav>
</header>

<div class="layout-container">
	<div class="sidebar">
		<h3>Quick Links</h3>
		<ul>
			<li><a href="#">HR Portal</a></li>
			<li><a href="#">IT Support</a></li>
			<li><a href="#">Finance</a></li>
		</ul>
	</div>

	<div class="main-content">
		<!-- React mounts here -->
		<div id="root"></div>

		<!-- ⚠️ REQUIRED: Liferay needs at least one column placeholder -->
		<div class="portlet-column" id="column-1">
			${processor.processColumn("column-1", "portlet-column-content")}
		</div>
	</div>
</div>

<footer style="text-align: center; padding: 1rem; background: #eee;">
	© 2025 My Company. All rights reserved.
</footer>

<script src="/o/my-react-spa/assets/index.js"></script>
</body>
</html>