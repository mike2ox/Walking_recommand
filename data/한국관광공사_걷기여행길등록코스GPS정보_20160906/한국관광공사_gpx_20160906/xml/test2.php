<?php

function data($gpxFilename) {
    
    
    
    if(strpos($gpxFilename, ".gpx")) {
        
        $Point = array();
         
        $xml = simplexml_load_file($gpxFilename);
        
        $points = $xml->trk->trkseg->trkpt;
        
        $i=0;
        
        foreach ($points as $p) {
            $attributes = $p->attributes();
            $Point[$i][lat] = (float) $attributes->lat;
            $Point[$i][long] = (float) $attributes->lon;
            $i++;
        }
    } else if(strpos($gpxFilename, ".kml")) {
        
        $Point = array();
        
        $xml = @file_get_contents($_SERVER['DOCUMENT_ROOT'].'/upload/xml/'.$gpxFilename);
        
        if($xml){
                if (strpos($xml, "google.com/kml/") != false) { //정상적으로 읽은 KML 파일이면 google.com/kml/가 들어가여 함
                    $Point = AddRoute($xml, "<coordinates>", "</coordinates>");
                } else {
                    require_once $_SERVER['DOCUMENT_ROOT']."/_include/xmlParser.php";
                    $parser = new XMLParser($xml); 
                    $parser->Parse();
                    $wpt = $parser->document->wpt;
        
                    $max_lat = '';
                    $min_lat = '';
                    $max_lon = '';
                    $min_lon = '';
        
                    $k = 0;
                    for($i=0;$i<count($wpt);$i++){
                        $lon = $wpt[$i]->tagAttrs['lon'];
                        $lat = $wpt[$i]->tagAttrs['lat'];
        
                        if(is_numeric($lat)&&is_numeric($lon)) {
                            $Point[$k]['lat'] = $lat;
                            $Point[$k]['lon'] = $lon;
                            $k++; 
                        }
                    }
                }
            }
    } 
    
    return $Point;
}

function AddRoute($str, $start, $end) {
        $returnArray = '';
        $start_pos = strpos($str, $start); 
        $end_pos = strpos($str, $end);
        
        $temp = substr($str, $start_pos, $end_pos-$start_pos);
        $temp = str_replace("<coordinates>", "", $temp);
        
        $temp = explode(" ", $temp);
        $count_temp = count($temp);

        $k = 0;
        for ($i=0; $i<$count_temp-1; $i++)  {
            $latlng = explode(",", $temp[$i]);
            $lon = $latlng[0];
            $lat = $latlng[1];

            if(is_numeric($lat)&&is_numeric($lon)) {
                $returnArray[$k]['lat'] = $lat;
                $returnArray[$k]['lon'] = $lon;
                $k++;
            }
        }
        return $returnArray;
}

?>
<script type="text/javascript" src="/js/jquery.js"></script>
<div id="map" style="background:#f00000;">ddd</div>
<script>
    $("#map").css("width","100px");
    $("#map").css("height","100px");
</script>
