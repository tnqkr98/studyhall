package com.example.test3;

public class Member_Frag_asset_Item {
    private String assetName;
    //private int date;
    private int[] timeTable = new int[25];

    public Member_Frag_asset_Item(String name){   // 생성자
        this.assetName = name;
        //this.date = date;
        for(int i=0;i<25;i++){
            timeTable[i] = 0;    // 예약 되어있으면 1, 예약 되어있지 않으면 0
        }
    }

    public void setTime(int startTimeP, int endTimeP){        // 최초 시설물시간 초기화
        for(int i = startTimeP;i <= endTimeP;i++) {
            timeTable[i] = 1;
        }
    }

    public void setAlltimeClear(){   //타임테이블 모두 비어있게 설정.
        for(int i=0;i<25;i++){
            timeTable[i] = 0;
        }
    }

    public boolean addTimeCheck(int startTimeP, int endTimeP){  // 사용자가 예약을 추가할때 call
        for(int i = startTimeP;i <= endTimeP;i++) {
            if (timeTable[i] != 0)
                return false;
        }

        for(int i = startTimeP;i <= endTimeP;i++) {
            timeTable[i] = 1;
        }

        return true; // 예약 성공시 true
    }


    public boolean getTime(int time){        // 시설물의 시간현황 불러오기
        if(timeTable[time] == 1)
            return true;                     // 해당시간에 예약이 있으면 true
        else
            return false;                   // 해당시간에 예약이 없으면 false
    }

    public String getName(){
        return assetName;
    }
}
