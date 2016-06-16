function df1(from,a){
    bean.print("print from script"+a);
    bean.i = 7890;
    var d = new java.util.Date();

    bean.s = d.toString();
}

function df2(from,a){
    return from.nf1()+"   in script   "+a;
}