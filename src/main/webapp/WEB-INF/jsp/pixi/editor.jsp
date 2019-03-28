<!DOCTYPE html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width">
  <title>I'm Up ticket map editor</title>
  <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css"> 
  <link rel="stylesheet" type="text/css" href="pixi/app.css">  
  <script src="pixi/vendor.js"></script>
  <script src="pixi/venuemapeditor.js"></script>
</head>
<body>
<div>
 <input type="button" id="button_select" value="Select" onclick="require('venuemapeditor').setMode(4);" />
 <input type="button" id="button_polygon" value="Polygon" onclick="require('venuemapeditor').setMode(1);" />
 <input type="button" id="button_rectabgle" value="Rectangle" onclick="require('venuemapeditor').setMode(2);" />
 <input type="button" id="button_seat" value="Seats" onclick="require('venuemapeditor').setMode(7);" /> 
 <input type="button" id="button_navigation" value="Navigation" onclick="require('venuemapeditor').setMode(5);" /> 
 <input type="button" id="button_delete" value="Delete" onclick="require('venuemapeditor').deleteSelectedShapes();" />
 <input type="button" id="button_open_zone" value="Open Zone" onclick=require('venuemapeditor').openZone(2,100,100,null,'{\"zones\":[{\"backgroundMediaId\":null,\"eventTicketId\":null,\"name\":null,\"descrition\":null,\"code\":null,\"capacity\":0,\"ordered\":0,\"shape\":{\"zoneId\":\"3\",\"width\":0,\"height\":0,\"path\":[{\"x\":296.56,\"y\":88.25},{\"x\":493.6,\"y\":246.95},{\"x\":416.44,\"y\":342.75},{\"x\":219.4,\"y\":184.05}]},\"available\":0,\"width\":1000,\"height\":800,\"seats\":[],\"zoneId\":\"3\"}],\"places\":[]}'); /> 
 <input type="button" id="button_select_zone" value="Select Zone" onclick="require('venuemapeditor').selectZone('-IMUPID1');" />
  
 <input type="button" id="button_export" value="Export View" onclick="require('venuemapeditor').exportZoneView();" /> 

 <input type="button" id="button_resize" value="Set Zone View Color" onclick="require('venuemapeditor').setZoneView('-IMUPID1',800,300,0xFFFF00,'data:image/gif;base64,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','{}'); "/> 

 <input type="button" id="button_svg" value="SVG" onclick=require('venuemapeditor').importSVG(data); />
 <input type="button" id="button_pixisvg" value="Pixi PNG" onclick=require('venuemapeditor').testPixiPNG(); />
 <input type="button" id="button_svg" value="Duplicate" onclick=require('venuemapeditor').duplicate(); />
 
 <input type="button" id="button_undo" value="UNDO" onclick=require('venuemapeditor').undo(); />
 <input type="button" id="button_redo" value="REDO" onclick=require('venuemapeditor').redo(); /> 
 <input type="button" id="button_text" value="Seat Text On" onclick="require('venuemapeditor').setSeatNameCodeVisibility(true,false);" />
 <input type="button" id="button_text" value="Seat Text Off" onclick="require('venuemapeditor').setSeatTextFontSize(0.6);" />
 <input type="button" id="button_text" value="Change Seat Text" onclick="require('venuemapeditor').updateSeat(null);" />
 
</div>

<div style="width:80%; margin: auto;">
<div style="background-image: url(http://upload.wikimedia.org/wikipedia/commons/8/84/Konqi_svg.svg);">  
<div id="editor-area">

</div>
</div>
<script>
require('venuemapeditor').initialize();
var data='<svg id=\"svg2\" viewBox=\"0 0 200 300\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:inkscape=\"http://www.inkscape.org/namespaces/inkscape\"><g inkscape:label=\"Layer 1\" inkscape_groupmode=\"layer\" id=\"layer1\"><path style=\"fill:none;fill-rule:evenodd;stroke:#000000;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\"     d=\"M 140.20758,316.54413 177.16152,195.9004 c 0,0 0.54344,-63.03907 133.14286,-65.21283 132.59941,-2.17376 157.05422,39.1277 166.83615,64.12595 9.78192,24.99825 32.60641,137.49038 32.60641,137.49038 0,0 -108.68805,-2.7172 -110.86181,-2.7172 -2.17376,0 -23.91137,-61.95219 -28.25889,-62.49563 -4.34752,-0.54344 -89.1242,0.54344 -89.1242,0.54344 l -26.62857,58.69155 z\" id=\"path3363\" inkscape:connector-curvature=\"0\" /></g></svg>';
//var data='<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"1630px\" height=\"790px\" viewBox="0 0  1630 790" preserveAspectRatio=\"xMidYMid meet\" ><polyline style=\"stroke:black;fill:none;stroke-width:1px;\" id=\"e1_polyline\" points=\"31 15 8 76 121 121 170 15 83 11\" /><path d=\"M33,17a26.342681,26.342681,0,0,0,52,-5\" style=\"fill:none;stroke:black;stroke-width:1px;\" id=\"e2_circleArc\"/></svg>';

</script>
</div>
</body>
