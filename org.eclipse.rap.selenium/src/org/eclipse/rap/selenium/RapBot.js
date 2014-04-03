(function(){

RapBot = {

  busy : false, // will be true during requests

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
     this.xpathToElement( element ).dispatchEvent( event );
  },

  xpathToElement : function( xpath ) {
    if( typeof xpath === "string" ) {
      return document.evaluate( xpath, document, null, XPathResult.ANY_TYPE, null ).iterateNext();
    } else {
      return xpath;
    }
  },

  getElementsByXPath : function( xpath ) {
    var set = document.evaluate( xpath, document, null, XPathResult.ANY_TYPE, null );
    var result = [];
    var element = set.iterateNext();
    while( element ) {
      result.push( element );
      element = set.iterateNext();
    }
    return result;
  }

};

rap.on( "send", function() {
  RapBot.busy = true;
} );

rap.on( "render", function() {
  RapBot.busy = false;
} );

// NOTE: The following patches modify RAP internals and may break in future
// versions.

// Selenium mouseXYZ methods may not set pageX/Y which can crash the server
// due to reported mouse coordinates [ null, null ]
rwt.qx.Class.__initializeClass( rwt.event.MouseEvent );
var proto = rwt.event.MouseEvent.prototype;
var wrap = function( name ) {
  var org = proto[ name ];
  proto[ name ] = function() {
    var result = org.apply( this, arguments ) || 0;
    return result;
  };
};
wrap( 'getScreenX' );
wrap( 'getScreenY' );
wrap( 'getClientX' );
wrap( 'getClientY' );
wrap( 'getPageX' );
wrap( 'getPageY' );

// Force empty grid cells to always be rendered in DOM
//rwt.qx.Class.__initializeClass( rwt.widgets.GridItem );
//rwt.widgets.GridItem.prototype.hasText = function(){ return true };


}());
