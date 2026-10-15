let lon = -57.54;
let lat = -38.00;
const signos=[{g:"♈"},{g:"♉"},{g:"♊"},{g:"♋"},{g:"♌"},{g:"♍"},{g:"♎"},{g:"♏"},{g:"♐"},{g:"♑"},{g:"♒"},{g:"♓"}];

function getLST(lng){
  const JD = Date.now()/86400000+2440587.5;
  let GMST = 280.46061837 + 360.98564736629*(JD-2451545);
  GMST = ((GMST%360)+360)%360;
  let LST = (GMST+lng)%360;
  if(LST<0) LST+=360;
  return LST;
}

function actualizar(){
  const now=new Date();
  document.getElementById('local').textContent=String(now.getHours()).padStart(2,'0')+':'+String(now.getMinutes()).padStart(2,'0');

  const day=Math.floor((now-new Date(now.getFullYear(),0,0))/86400000);
  const B=(360/365)*(day-81)*Math.PI/180;
  const EoT=9.87*Math.sin(2*B)-7.53*Math.cos(B)-1.5*Math.sin(B);
  let sMin=now.getHours()*60+now.getMinutes()+lon*4+EoT+180;
  sMin=((sMin%1440)+1440)%1440;
  document.getElementById('solar').textContent=String(Math.floor(sMin/60)).padStart(2,'0')+':'+String(Math.floor(sMin%60)).padStart(2,'0');

  const LST=getLST(lon);
  const h=LST/15;
  document.getElementById('sideral').textContent=String(Math.floor(h)).padStart(2,'0')+':'+String(Math.floor((h-Math.floor(h))*60)).padStart(2,'0');
  document.getElementById('signo').textContent=`${Math.floor(LST%30)}° ${signos[Math.floor(LST/30)].g}`;
}

document.getElementById('coords').textContent=`${lat.toFixed(1)}° ${lon.toFixed(1)}°`;
if(navigator.geolocation){
  navigator.geolocation.getCurrentPosition(p=>{
    lon=p.coords.longitude; lat=p.coords.latitude;
    document.getElementById('coords').textContent=`${lat.toFixed(1)}° ${lon.toFixed(1)}°`;
    actualizar();
  },null,{enableHighAccuracy:false,timeout:4000,maximumAge:600000});
}
actualizar();
setInterval(actualizar,10000);