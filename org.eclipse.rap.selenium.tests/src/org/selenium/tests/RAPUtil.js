(function(){

RAPUtil = {

  busy : false,

  /*global WheelEvent:false, MouseWheelEvent:false */
  scrollWheel : function( element, delta ) {
    var event;
    if( WheelEvent && WheelEvent.prototype.initWebKitWheelEvent ) { // webkit
      event = document.createEvent( "WheelEvent" );
      event.initWebKitWheelEvent( 0, //wheelDeltaX
                                  delta, // wheelDeltaY
                                  window, // view
                                  0, // screenX
                                  0, // screenY
                                  0, // clientX
                                  0, // clientY
                                  false, // ctrlKey
                                  false, // altKey
                                  false, // shiftKey
                                  false ); // metaKey
     } else if( MouseWheelEvent && MouseWheelEvent.prototype.initMouseWheelEvent ) { // IE9+
       event = document.createEvent( "MouseWheelEvent" );
       event.initMouseWheelEvent( "mousewheel", // eventType
                                   true, // canBubble,
                                   true, // cancelable,
                                   window, // view,
                                   0, // detail,
                                   0, // screenXArg,
                                   0, // screenYArg,
                                   0, // clientXArg,
                                   0, // clientYArg,
                                   0, // buttonArg,
                                   null, // relatedTargetArg,
                                   "", // modifiersListArg,
                                   delta * 120 ); // wheelDeltaArg
     } else { // assumes firefox
       event = document.createEvent( "MouseEvents" );
       event.initMouseEvent( "DOMMouseScroll", // typeArg
                             true, // canBubbleArg
                             true, // cancelableArg
                             window, // viewArg
                             delta * -3, // detailArg
                             0, // screenXArg
                             0, // screenYArg
                             0, // clientXArg
                             0, // clientYArg
                             false, // ctrlKeyArg
                             false, // altKeyArg
                             false, // shiftKeyArg
                             false, // metaKeyArg
                             0, // buttonArg
                             null ); // relatedTargetArg
     }
     xpathToElement( element ).dispatchEvent( event );
  }

};

rap.on( "send", function() {
  RAPUtil.busy = true;
} );

rap.on( "render", function() {
  RAPUtil.busy = false;
} );

function xpathToElement( xpath ) {
  if( typeof xpath === "string" ) {
    return document.evaluate( xpath, document, null, XPathResult.ANY_TYPE, null ).iterateNext();
  } else {
    return xpath;
  }
}

}());
