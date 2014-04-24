<%@ tag description="Simple header to indicate the current page" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="topicTitle" required="true" %>
<%@ attribute name="pageTitle" required="true" %>

<section class="section-title-container teal">
    <span>${topicTitle}</span>
</section>

<section class="bread-crumb-container teal">
    <span>${pageTitle}</span>
</section>