<?php

  /*   
    function data($filename) {
    
        
        if(strpos($filename, ".gpx")) {
            $Point = array();
             
            $xml = simplexml_load_file($_SERVER['DOCUMENT_ROOT'].'/upload/xml/'.$filename);
            
            $points = $xml->trk->trkseg->trkpt;
            
            $i=0;
            
            foreach ($points as $p) {
                $attributes = $p->attributes();
                $Point[$i][lat] = (float) $attributes->lat;
                $Point[$i][long] = (float) $attributes->lon;
                $i++;
            }
        }
        
        return $Point;
    }
    
   $test = data("2.gpx");
    
    echo "====================".$test[10][lat];
   *
   */
?>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js"></script>
<script>
function map1(){
    var param = "";
    param += "gpx=list1_1.gpx";
    var request = $.ajax({
    url:"http://koreatrails.linux.gabiauser.com/upload/xml/data.php",
    type:"GET",
    data: param
    });
    request.done(function(data){
        alert(data.point.length);
        
        
    });
}    

map1();
    
</script>