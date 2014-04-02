/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.ui.forms.widgets.ToggleHyperlink", {
  extend : rwt.widgets.base.Image,

  construct : function() {
    this.base( arguments );
    this.setCursor( "pointer" );
    this._hover = false;
    this._expanded = false;
    this._collapseNormal = null;
    this._collapseHover = null;
    this._expandHover = null;
    this._expandNormal = null;
    this.addEventListener( "mousemove", this._onMouseMove, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
  },

  destruct : function() {
    this.removeEventListener( "mousemove", this._onMouseMove, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
  },

  members : {

    setImages : function( collapseNormal, collapseHover, expandNormal, expandHover ) {
      this._collapseNormal = collapseNormal;
      this._collapseHover = collapseHover != null ? collapseHover : collapseNormal;
      this._expandNormal = expandNormal;
      this._expandHover = expandHover != null ? expandHover : expandNormal;
      this._updateImage();
    },

    setExpanded : function( value ) {
      this._expanded = value;
      this._updateImage();
    },

    setHasDefaultSelectionListener : function( value ) {
      if( value ) {
        this.addEventListener( "click", rwt.remote.EventUtil.widgetDefaultSelected, this );
      } else {
        this.removeEventListener( "click", rwt.remote.EventUtil.widgetDefaultSelected, this );
      }
    },

    _onMouseMove : function( evt ) {
      this._hover = true;
      this._updateImage();
    },

    _onMouseOut : function( evt ) {
      this._hover = false;
      this._updateImage();
    },

    _updateImage : function() {
      var source;
      if( this._expanded ) {
        source = this._hover ? this._collapseHover : this._collapseNormal;
      } else {
        source = this._hover ? this._expandHover : this._expandNormal;
      }
      this.setSource( source );
    }

  }

} );


/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.remote.HandlerRegistry.add( "forms.widgets.ToggleHyperlink", {

  factory : function( properties ) {
    var result = new org.eclipse.ui.forms.widgets.ToggleHyperlink();
    result.setUserData( "isControl", true );
    rwt.remote.HandlerUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.remote.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.remote.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.remote.HandlerUtil.extendControlProperties( [
    "images",
    "expanded"
  ] ),

  propertyHandler : rwt.remote.HandlerUtil.extendControlPropertyHandler( {
    "images" : function( widget, value ) {
      var collapseNormal = value[ 0 ] === null ? null : value[ 0 ][ 0 ];
      var collapseHover = value[ 1 ] === null ? null : value[ 1 ][ 0 ];
      var expandNormal = value[ 2 ] === null ? null : value[ 2 ][ 0 ];
      var expandHover = value[ 3 ] === null ? null : value[ 3 ][ 0 ];
      widget.setImages( collapseNormal, collapseHover, expandNormal, expandHover );
    }
  } ),

  listeners : rwt.remote.HandlerUtil.extendControlListeners( [
    "DefaultSelection"
  ] ),

  listenerHandler : rwt.remote.HandlerUtil.extendControlListenerHandler( {} ),

  methods : []

} );
/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.ui.forms.widgets.Hyperlink", {
  extend : rwt.widgets.base.Atom,

  construct : function( style ) {
    this.base( arguments );
    this.setAppearance( "hyperlink" );
    // TODO [rh] workaround for weird getLabelObject behavior
    this.setLabel( "(empty)" );
    // End of workaround
    var labelObject = this.getLabelObject();
    labelObject.setAppearance( "hyperlink-label" );
    labelObject.setMode( "html" );
    labelObject.setWrap( rwt.util.Strings.contains( style, "wrap" ) );
    // TODO [rh] workaround for weird getLabelObject behavior
    this.setLabel( "" );
    // End of workaround
    this._text = "";
    this._underlined = false;
    this._savedBackgroundColor = null;
    this._savedTextColor = null;
    this._activeBackgroundColor = null;
    this._activeTextColor = null;
    this._underlineMode = null;
    this._hover = false;
    this.addEventListener( "mousemove", this._onMouseMove, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
  },

  destruct : function() {
    this.removeEventListener( "mousemove", this._onMouseMove, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
  },

  statics : {
    UNDERLINE_NEVER : 1,
    UNDERLINE_HOVER : 2,
    UNDERLINE_ALWAYS : 3
  },

  members : {

    setText : function( value ) {
      this._text = value;
      this._updateText();
    },

    setUnderlined : function( value ) {
      this._underlined = value;
      this._updateText();
    },

    _updateText : function() {
      var text = this._underlined ? "<u>" + this._text + "</u>" : this._text;
      this.setLabel( text );
    },

    setActiveBackgroundColor : function( value ) {
      this._activeBackgroundColor = value;
    },

    setActiveTextColor : function( value ) {
      this._activeTextColor = value;
    },

    setUnderlineMode : function( value ) {
      this._underlineMode = value;
    },

    setHasDefaultSelectionListener : function( value ) {
      if( value ) {
        this.addEventListener( "click", rwt.remote.EventUtil.widgetDefaultSelected, this );
      } else {
        this.removeEventListener( "click", rwt.remote.EventUtil.widgetDefaultSelected, this );
      }
    },

    _onMouseMove : function( evt ) {
      if( !this._hover ) {
        this._savedBackgroundColor = this.getBackgroundColor();
        if( this._activeBackgroundColor != null ) {
          this.setBackgroundColor( this._activeBackgroundColor );
        }
        this._savedTextColor = this.getTextColor();
        if( this._activeTextColor != null ) {
          this.setTextColor( this._activeTextColor );
        }
        var mode = org.eclipse.ui.forms.widgets.Hyperlink.UNDERLINE_HOVER;
        if( this._underlineMode == mode ) {
          this.setStyleProperty( "textDecoration", "underline");
        }
        this._hover = true;
      }
    },

    _onMouseOut : function( evt ) {
      if( this._hover ) {
        this._hover = false;
        this.setBackgroundColor( this._savedBackgroundColor );
        this.setTextColor( this._savedTextColor );
        var mode = org.eclipse.ui.forms.widgets.Hyperlink.UNDERLINE_HOVER;
        if( this._underlineMode == mode ) {
          this.setStyleProperty( "textDecoration", "none");
        }
      }
    }
  }

} );

/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.remote.HandlerRegistry.add( "forms.widgets.Hyperlink", {

  factory : function( properties ) {
    var wrap = properties.style.indexOf( "WRAP" ) !== -1 ? "wrap" : "";
    var result = new org.eclipse.ui.forms.widgets.Hyperlink( wrap );
    rwt.remote.HandlerUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.remote.HandlerUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.remote.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.remote.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.remote.HandlerUtil.extendControlProperties( [
    "text",
    "image",
    "underlined",
    "underlineMode",
    "activeForeground",
    "activeBackground"
  ] ),

  propertyHandler : rwt.remote.HandlerUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      var EncodingUtil = rwt.util.Encoding;
      var text = EncodingUtil.escapeText( value, false );
      widget.setText( text );
    },
    "image" : function( widget, value ) {
      if( value === null ) {
        widget.setIcon( null );
      } else {
        widget.setIcon( value[ 0 ] );
      }
    },
    "activeForeground" : function( widget, value ) {
      if( value === null ) {
        widget.setActiveTextColor( null );
      } else {
        widget.setActiveTextColor( rwt.util.Colors.rgbToRgbString( value ) );
      }
    },
    "activeBackground" : function( widget, value ) {
      if( value === null ) {
        widget.setActiveBackgroundColor( null );
      } else {
        widget.setActiveBackgroundColor( rwt.util.Colors.rgbToRgbString( value ) );
      }
    }
  } ),

  listeners : rwt.remote.HandlerUtil.extendControlListeners( [
    "DefaultSelection"
  ] ),

  listenerHandler : rwt.remote.HandlerUtil.extendControlListenerHandler( {} ),

  methods : []

} );
/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.ui.forms.widgets.FormText", {
  extend : rwt.widgets.base.Parent,

  construct : function() {
    this.base( arguments );
    this.setAppearance( "formtext" );
    this._hyperlinks = [];
    this._segments = [];
    this._hyperlinkMode = org.eclipse.ui.forms.widgets.FormText.UNDERLINE_ALWAYS;
    this._hyperlinkForeground = "#0000FF";
    this._hyperlinkActiveForeground = "#0000FF";
  },

  destruct : function() {
    this.clearContent();
  },

  statics : {
    UNDERLINE_NEVER : 1,
    UNDERLINE_HOVER : 2,
    UNDERLINE_ALWAYS : 3
  },

  members : {

    createBullet : function( style, image, text, bounds ) {
      var bullet = new rwt.widgets.base.Atom();
      bullet.setAppearance( "formtext-bullet" );
      switch( style ) {
        case 2:
          bullet.setLabel( text );
          break;
        case 3:
          bullet.setIcon( image );
          break;
        default:
          bullet.setIcon( image );
      }
      bullet.set( {
        left   : bounds[ 0 ],
        top    : bounds[ 1 ],
        width  : bounds[ 2 ],
        height : bounds[ 3 ]
      } );
      this._segments[ this._segments.length ] = bullet;
      this.add( bullet );
    },

    createTextHyperlinkSegment : function( text, toolTip, bounds, font ) {
      var textHyperlink = new rwt.widgets.base.Label();
      textHyperlink.setAppearance( "formtext-hyperlink" );
      var escapedText = this._escapeText( text );
      textHyperlink.set( {
        text   : escapedText,
        left   : bounds[ 0 ],
        top    : bounds[ 1 ],
        width  : bounds[ 2 ],
        height : bounds[ 3 ]
      } );
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      widgetManager.setToolTip( textHyperlink, toolTip );
      if( font != null ) {
        widgetManager.setFont( textHyperlink, font[ 0 ], font[ 1 ], font[ 2 ], font[ 3 ] );
      }
      textHyperlink.addEventListener( "mousemove", this._onMouseMove, this );
      textHyperlink.addEventListener( "mouseout", this._onMouseOut, this );
      this._hyperlinks[ this._hyperlinks.length ] = textHyperlink;
      this.add( textHyperlink );
    },

    createTextSegment : function( text, bounds, font, color ) {
      var textFragment = new rwt.widgets.base.Label();
      textFragment.setAppearance( "formtext-text" );
      var escapedText = this._escapeText( text );
      textFragment.set( {
        text   : escapedText,
        left   : bounds[ 0 ],
        top    : bounds[ 1 ],
        width  : bounds[ 2 ],
        height : bounds[ 3 ]
      } );
      if( font != null ) {
        textFragment.setFont( rwt.html.Font.fromArray( font ) );
      }
      if( color != null ) {
        textFragment.setTextColor( rwt.util.Colors.rgbToRgbString( color ) );
      }
      this._segments[ this._segments.length ] = textFragment;
      this.add( textFragment );
    },

    createImageHyperlinkSegment : function( source, toolTip, bounds ) {
      var imageHyperlink = new rwt.widgets.base.Image();
      imageHyperlink.setAppearance( "formtext-hyperlink" );
      imageHyperlink.set( {
        source     : source,
        left       : bounds[ 0 ],
        top        : bounds[ 1 ],
        width      : bounds[ 2 ],
        height     : bounds[ 3 ],
        paddingTop : 2
      } );
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      widgetManager.setToolTip( imageHyperlink, toolTip );
      this._segments[ this._segments.length ] = imageHyperlink;
      this.add( imageHyperlink );
    },

    createImageSegment : function( source, bounds ) {
      var image = new rwt.widgets.base.Image();
      image.setAppearance( "formtext-image" );
      image.set( {
        source : source,
        left   : bounds[ 0 ],
        top    : bounds[ 1 ],
        width  : bounds[ 2 ],
        height : bounds[ 3 ]
      } );
      this._segments[ this._segments.length ] = image;
      this.add( image );
    },

    setHyperlinkSettings : function( mode, foreground, activeForeground ) {
      this._hyperlinkMode = mode;
      if( foreground != null ) {
        this._hyperlinkForeground = foreground;
      }
      if( activeForeground != null ) {
        this._hyperlinkActiveForeground = activeForeground;
      }
      this.updateHyperlinks();
    },

    updateHyperlinks : function() {
      for( var i = 0; i < this._hyperlinks.length; i++ ) {
        this._hyperlinks[ i ].setTextColor( this._hyperlinkForeground );
        if( this._hyperlinkMode == org.eclipse.ui.forms.widgets.FormText.UNDERLINE_ALWAYS ) {
          this._hyperlinks[ i ].setStyleProperty( "textDecoration", "underline");
        }
      }
    },

    clearContent : function() {
      for( var i = 0; i < this._hyperlinks.length; i++ ) {
        this._hyperlinks[ i ].removeEventListener( "mousemove",
                                                   this._onMouseMove,
                                                   this );
        this._hyperlinks[ i ].removeEventListener( "mouseout",
                                                   this._onMouseOut,
                                                   this );
        this._hyperlinks[ i ].destroy();
      }
      for( var i = 0; i < this._segments.length; i++ ) {
        this._segments[ i ].destroy();
      }
      this._hyperlinks = [];
      this._segments = [];
    },

    _escapeText : function( text ) {
      return text.replace( / /g, "&nbsp;" );
    },

    _onMouseMove : function( evt ) {
      var hyperlink = evt.getTarget();
      if( !this.hasState( "hover" ) ) {
        this.addState( "hover" );
        hyperlink.setTextColor( this._hyperlinkActiveForeground );
        if( this._hyperlinkMode == org.eclipse.ui.forms.widgets.FormText.UNDERLINE_HOVER ) {
          hyperlink.setStyleProperty( "textDecoration", "underline");
        }
      }
    },

    _onMouseOut : function( evt ) {
      var hyperlink = evt.getTarget();
      if( this.hasState( "hover" ) ) {
        this.removeState( "hover" );
        hyperlink.setTextColor( this._hyperlinkForeground );
        if( this._hyperlinkMode == org.eclipse.ui.forms.widgets.FormText.UNDERLINE_HOVER ) {
          hyperlink.setStyleProperty( "textDecoration", "none");
        }
      }
    }
  }
} );

/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.remote.HandlerRegistry.add( "forms.widgets.FormText", {

  factory : function( properties ) {
    var result = new org.eclipse.ui.forms.widgets.FormText();
    result.setUserData( "isControl", true );
    rwt.remote.HandlerUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.remote.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.remote.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.remote.HandlerUtil.extendControlProperties( [
    "text",
    "hyperlinkSettings"
  ] ),

  propertyHandler : rwt.remote.HandlerUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      widget.clearContent();
      for( var i = 0; i < value.length; i++ ) {
        var type = value[ i ][ 0 ];
        var args = value[ i ].slice( 1 );
        switch( type ) {
          case "bullet":
            widget.createBullet.apply( widget, args );
          break;
          case "textHyperlink":
            widget.createTextHyperlinkSegment.apply( widget, args );
          break;
          case "text":
            widget.createTextSegment.apply( widget, args );
          break;
          case "imageHyperlink":
            widget.createImageHyperlinkSegment.apply( widget, args );
          break;
          case "image":
            widget.createImageSegment.apply( widget, args );
          break;
        }
      }
      widget.updateHyperlinks();
    },
    "hyperlinkSettings" : function( widget, value ) {
      var ColorUtil = rwt.util.Colors;
      var foreground = value[ 1 ] !== null ? ColorUtil.rgbToRgbString( value[ 1 ] ) : null;
      var activeForeground = value[ 2 ] !== null ? ColorUtil.rgbToRgbString( value[ 2 ] ) : null;
      widget.setHyperlinkSettings( value[ 0 ], foreground, activeForeground );
    }
  } ),

  listeners : rwt.remote.HandlerUtil.extendControlListeners( [] ),

  listenerHandler : rwt.remote.HandlerUtil.extendControlListenerHandler( {} ),

  methods : []

} );
