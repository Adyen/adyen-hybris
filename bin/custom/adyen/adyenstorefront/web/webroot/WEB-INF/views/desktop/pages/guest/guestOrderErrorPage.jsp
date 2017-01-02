<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>

<%--
  ~ /*
  ~  *
  ~  * [y] hybris Platform
  ~  *
  ~  * Copyright (c) 2000-2014 hybris AG
  ~  * All rights reserved.
  ~  *
  ~  * This software is the confidential and proprietary information of hybris
  ~  * ("Confidential Information"). You shall not disclose such Confidential
  ~  * Information and shall use it only in accordance with the terms of the
  ~  * license agreement you entered into with hybris.
  ~  *
  ~  */
  --%>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<div class="span-20 last">
		<div class="span-20 last">
            Guest Order Error Page
		</div>
	</div>
</template:page>