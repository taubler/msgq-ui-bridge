var eventBus = null;
var eventBusOpen = false;

function initWs() {
	eventBus = new EventBus('/client.register');
    eventBus.onopen = function() {
    	eventBusOpen = true;
    	regForMessages();
    };
    eventBus.onerror = function(err) {
        console.error(err);
    	eventBusOpen = false;
    };
}

function regForMessages() {
    if (eventBusOpen) {
    	eventBus.registerHandler('service.ui-message', function (error, message) {
            if (message) {
				console.info('Received message: ' + message);
				var msgList = $("div#messages");
				msgList.html(msgList.html() + "<div>" + message.body + "</div>");
            } else if (error) {
            	console.error(error);
            }        
        });
        if (loanGuid) {
	    	eventBus.registerHandler('service.ui-taskitem-' + loanGuid, function (error, message) {
	            if (message) {
					console.info('Received taskItem: ' + message);
					var taskItem = message.body;
					if (taskItem.taskId) {
						$("#" + taskItem.taskId).attr('src', (taskItem.completed == true) ? 'rsc/img/check_on.png' : 'rsc/img/check_off.png');
					} else {
						console.error("Non-conforming task item");
					}
	            } else if (error) {
	            	console.error(error);
	            }        
	        });
        }
    } else {
        console.error("Cannot register for messages; event bus is not open");
    }
}

$( document ).ready(function() {
	initWs();
});
	
function unregister() {
	reg().subscribe(null);
}