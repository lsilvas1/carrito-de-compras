let csrf;
const $=id=>document.getElementById(id),money=n=>'Q '+Number(n).toFixed(2);
function node(tag,text){const e=document.createElement(tag);if(text!==undefined)e.textContent=text;return e;}
function message(text,error=false){$('message').textContent=text;$('message').className=error?'error':'';}
async function api(path,method='GET',body){
 const headers={'Content-Type':'application/json'};
 if(method!=='GET')headers[csrf.headerName]=csrf.token;
 const r=await fetch('/api/'+path,{method,headers,credentials:'same-origin',body:body===undefined?undefined:JSON.stringify(body)});
 if(!r.ok){let e;try{e=await r.json();}catch{e={message:'Error '+r.status};}throw Error(e.message||'Error '+r.status);}
 return r.status===204?undefined:r.json();
}
async function action(fn){try{await fn();await refresh();}catch(e){message(e.message,true);}}
async function refresh(){
 const [products,cart]=await Promise.all([api('products'),api('cart')]);
 $('products').replaceChildren();
 for(const p of products){const card=node('article');card.className='card';const img=node('img');img.src=p.image;img.alt=p.name;
 const qty=node('input');qty.type='number';qty.min=1;qty.max=999;qty.value=1;qty.setAttribute('aria-label','Cantidad de '+p.name);
 const btn=node('button','Agregar');btn.onclick=()=>action(async()=>{await api('cart/items','POST',{productId:p.id,quantity:Number(qty.value)});message('Producto agregado');});
 const price=node('p',money(p.price));price.className='price';const controls=node('div');controls.className='actions';controls.append(qty,btn);
 card.append(img,node('h3',p.name),price,controls);$('products').append(card);}
 $('items').replaceChildren();if(!cart.items.length)$('items').append(node('p','Tu carrito está vacío.'));
 for(const i of cart.items){const row=node('div');row.className='item';const qty=node('input');qty.type='number';qty.min=1;qty.max=999;qty.value=i.quantity;qty.setAttribute('aria-label','Cantidad de '+i.name);
 const update=node('button','Actualizar');update.onclick=()=>action(()=>api('cart/items/'+i.productId,'PUT',{quantity:Number(qty.value)}));
 const remove=node('button','Quitar');remove.onclick=()=>action(()=>api('cart/items/'+i.productId,'DELETE'));
 row.append(node('span',i.name+' · '+money(i.subtotal)),qty,update,remove);$('items').append(row);}
 $('total').textContent='Total: '+money(cart.total);$('checkout').disabled=cart.items.length===0;$('clear').disabled=cart.items.length===0;

}
let buying=false;
$('checkout').onclick=()=>action(async()=>{if(buying)return;buying=true;$('checkout').disabled=true;try{const o=await api('cart/checkout','POST',{method:$('method').value});message('Pago simulado: '+money(o.total)+' · '+o.reference+'. No se guarda historial de pedidos.');}finally{buying=false;}});
$('clear').onclick=()=>action(()=>api('cart','DELETE'));
(async()=>{try{csrf=await api('csrf');await refresh();}catch(e){message(e.message,true);}})();
