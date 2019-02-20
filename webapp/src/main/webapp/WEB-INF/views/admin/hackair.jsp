<%--

    Copyright (C) 2012-2019 52°North Initiative for Geospatial Open Source
    Software GmbH

    This program is free software; you can redistribute it and/or modify it
    under the terms of the GNU General Public License version 2 as published
    by the Free Software Foundation.

    If the program is linked with libraries which are licensed under one of
    the following licenses, the combination of the program with the linked
    library is not considered a "derivative work" of the program:

        - Apache License, version 2.0
        - Apache Software License, version 1.0
        - GNU Lesser General Public License, version 3
        - Mozilla Public License, versions 1.0, 1.1 and 2.0
        - Common Development and Distribution License (CDDL), version 1.0

    Therefore the distribution of the program linked with libraries licensed
    under the aforementioned licenses, is permitted by the copyright holders
    if the distribution is compliant with both the GNU General Public
    License version 2 and the aforementioned licenses.

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
    Public License for more details.

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:include page="../common/header.jsp">
    <jsp:param name="activeMenu" value="admin" />
</jsp:include>

<link rel="stylesheet" href="<c:url value="/static/lib/prettify.css" />" type="text/css" />
<link rel="stylesheet" href="<c:url value="/static/lib/codemirror-2.34.css" />" type="text/css" />
<link rel="stylesheet" href="<c:url value="/static/css/codemirror.custom.css" />" type="text/css" />
<link rel="stylesheet" href="<c:url value="/static/lib/bootstrap-toggle-buttons.css" />" type="text/css" />
<script type="text/javascript" src="<c:url value="/static/lib/codemirror-2.34.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/codemirror-2.34-xml.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/prettify.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/vkbeautify-0.99.00.beta.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/lib/jquery.toggle.buttons.js" />"></script>

<script type="text/javascript" src="<c:url value="/static/js/jquery.additions.js" />"></script>
<script type="text/javascript" src="<c:url value="/static/js/EventMixin.js" />"></script>

<jsp:include page="../common/logotitle.jsp">
    <jsp:param name="title" value="HackAIR Settings" />
    <jsp:param name="leadParagraph" value="" />
</jsp:include>

<style type="text/css">
    .btn-icon { height: 30px; }
    .btn-icon i { margin-right: 0px !important; }
    .btn-single { margin-bottom: 1px;}
    #stcaps-publish { margin-bottom: -11px; }
    #back-top {
        position: fixed;
        bottom: 30px;
        margin-left: -150px;
    }
    #back-top a {
        width: 108px;
        display: block;
        text-align: center;
        font: 11px Arial, Helvetica, sans-serif;
        text-transform: uppercase;
        text-decoration: none;
        color: #bbb;
        /* background color transition */
        -webkit-transition: 1s;
        -moz-transition: 1s;
        transition: 1s;
    }
    #back-top a:hover {
        color: #000;
    }
    #back-top i {
        display: block;
        margin-bottom: 7px;
        margin-left: 48px;
        -webkit-border-radius: 15px;
        -moz-border-radius: 15px;
        border-radius: 15px;
        -webkit-transition: 1s;
        -moz-transition: 1s;
        transition: 1s;
    }
</style>

<script type="text/javascript">
    jQuery(document).ready(function($) {
        $(window).scroll(function () {
            var pos = $(this).scrollTop();
            if (pos > "fast" && pos < $(document).height() 
                                    - ($(this).height() + 400)) {
                $('#back-top').fadeIn();
            } else {
                $('#back-top').fadeOut();
            }
        });
        $('#back-top a').click(function () {
            $('body, html').animate({
                scrollTop: 0
            }, 400);
            return false;
        });
    });

    function xml2string(xml) {
        return typeof(xml) === "string" ? xml : xml.xml ? xml.xml 
                : new XMLSerializer().serializeToString(xml); 
    }
</script>

<div id="hackair-container">
	<div class="row">
	<%--
		<div class="span12 form-inline" style="margin-bottom: 5px;">
			<select id="id" class="span6">
				<option disabled="disabled" selected="selected" style="display: none;" value="">${active}</option>
			</select>
		</div>
		--%>
		<div class="btn-group">
        <button id="start" title="Start HackAIR harvesting" type="button" class="btn btn-icon stcaps-edit-button">Start</button>
        <button id="update" title="Update HackAIR harvesting (Reload the config file)" type="button" name="" class="btn btn-icon stcaps-edit-button">Update</button>
        <button id="stop" title="Stop HackAIR harvesting" type="button" name="" class="btn btn-icon stcaps-edit-button">Stop</button>
      </div>
	</div>
	<%--<textarea id="description" readonly="readonly" class="span12"></textarea> --%>
</div>

<p id="back-top" style="display: none;"><a href="#top"><i class="icon-chevron-up"></i>Back to Top</a></p>

<script type="text/javascript">
function Descriptions(options) {
	$.extend(this, options);
}

$.extend(Descriptions.prototype, {
	get: function(id, onSuccess, context) {
		if (arguments.length === 0) {
			return this.profiles;
		}
		<%-- $.ajax({
             "url": "<c:url value="/admin/profiles/description"/>",
             "type": "GET",
             "data": {
                 "identifier": id
             }
         }).done(function(e){
             showSuccess("Renamed <code>" + oldName + "</code> to <code>" + newName + "</code>");
             $(".observableProperty input").trigger("change");
         }).fail(function(e){
             showError(e.responseText);
         });--%>
	},
	start: function(id, context) {
		if (arguments.length === 0) {
			return this.profiles;
		}
		 $.ajax({
             "url": "<c:url value="/admin/hackair/start"/>",
             "type": "GET"
         }).done(function(e){
             showSuccess("HackAIR harvesting started!");
             $(".observableProperty input").trigger("change");
         }).fail(function(e){
             showError(e.responseText);
         });
	}
	
});

function Controller(options) {
	$.extend(this, options);
	$.extend(this, {
		$start: $("#start"),
		$update: $("#update"),
		$stop: $("#stop"),
		$profile: $("#id"),
		$description: $("#description"),
		$container: $("#hackair-container")
	});
	this.init();
}

$.extend(Controller.prototype, {
	init: function() {
		var i, self = this,
			profiles = this.descriptions.get();

		this.$description.codeMirror({
			mode: "xml",
			lineNumbers: true,
			lineWrapping: true
		});
		this.$start.on("click", function() {
			self.onStart.apply(self, arguments);
		});
		this.$update.on("click", function() {
			self.onUpdate.apply(self, arguments);
		});
		this.$stop.on("click", function() {
      self.onStop.apply(self, arguments);
    });
		for (i = 0; i < profiles.length; ++i) {
			if (profiles[i] != (this.$activate)) {
				$("<option>").text(profiles[i]).appendTo(this.$profile);
			}
		}
		this.$profile.trigger("change");
		this.setEditorContent(vkbeautify.xml(xml2string("sdakfkasdhfk")));

	},
	getSelectedProfile: function() {
		return this.$profile.val();
	},
	setEditorContent: function(x) {
		this.$description.codeMirror("set", x);
	},
	onIdChange: function() {
	},
	onStart: function() {
		$.ajax({
            "url": "<c:url value="/admin/hackair/start"/>",
            "type": "GET"
        }).fail(function(e) {
          showError("Failed to start: " 
                  + e.status + " " + e.statusText);
        }).done(function() {
          showSuccess("HackAIR harvesting successfully started!");
        });
	},
	onUpdate: function() {
		$.ajax({
            "url": "<c:url value="/admin/hackair/update"/>",
            "type": "GET"
        }).fail(function(e) {
	        showError("Failed to save update: " 
	                + e.status + " " + e.statusText);
        }).done(function() {
        	showSuccess("HackAIR harvesting successfully updated!");
        });
	},
	onStop: function() {
	    $.ajax({
	            "url": "<c:url value="/admin/hackair/stop"/>",
	            "type": "GET"
	        }).fail(function(e) {
	          showError("Failed to stop: " 
	                  + e.status + " " + e.statusText);
	        }).done(function() {
	          showSuccess("HackAIR harvesting successfully stopped!");
	        });
	  }
});
</script>

<script type="text/javascript">
	var active = "${active}";
	var profiles = [];
	<c:forEach items="${profiles}" var="p">
	profiles.push("${p}");</c:forEach>

	var baseUrl = "<c:url value="/"/>";
	var descriptions = new Descriptions({
		baseUrl: baseUrl,
		profiles: profiles
	});
	new Controller({
		baseUrl: baseUrl,
		descriptions: descriptions
	});
</script>

<jsp:include page="../common/footer.jsp" />