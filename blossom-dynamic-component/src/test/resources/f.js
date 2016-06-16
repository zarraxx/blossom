var d;

var TestClassA = Java.type("cn.net.xyan.blossom.script.test.TestClassA");

function fetch(value, count) {
    count++ ;
    return {value: value, count : count}
};



function func(value, count) {
    count++ ;
    return {value: value, count : count}
};

function setter(v){
    print(v);
    c['var1'] = v;
}
function getter(){
    print("getter");
    c.hello();
    return c['var1'];
}


var Inhert = Java.extend(TestClassA, {
    var1:undefined,
    var2:undefined,
    hello: function() {
        print("Run in separate thread");
        return "hello from js";
    }
});

c = new Inhert();

