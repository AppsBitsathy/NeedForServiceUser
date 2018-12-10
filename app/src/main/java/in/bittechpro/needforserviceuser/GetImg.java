package in.bittechpro.needforserviceuser;

class GetImg {
    private Integer[] list = {
            R.drawable.washbasin,
            R.drawable.shower,
            R.drawable.urinal
    };
    GetImg() {
    }
    int getImg(String s){
        if(s.contains("W"))
            return list[0];
        else if(s.contains("B"))
            return list[1];
        else
            return  list[2];
    }
}
