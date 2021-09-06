package com.example.desgarron.Logic.SigurComponents;


import android.util.Log;

import com.example.desgarron.Log.DesgarronLog;
import com.example.desgarron.Models.Direction;
import com.example.desgarron.Models.Emp2_0;
import com.example.desgarron.Models.EmpSigurBASE;
import com.example.desgarron.Models.InPacket;

import org.json.JSONException;
import org.json.JSONObject;

public class PacketManager {


    public static boolean onLoginAwaited(InPacket inPacket) {
        if (inPacket == null) {
            throw new IllegalArgumentException();
        } else if (inPacket.getType() != 2) {
            throw new IllegalArgumentException();
        } else if (inPacket.getCode() != 108) {
            throw new IllegalArgumentException();
        } else {
            int extractInt = inPacket.extractInt();
            Log.d("desdogs2",extractInt+"");
            if (extractInt == 11) {
                DesgarronLog.append("parseResponse(). licence violation error");
                return false;
            } else if (extractInt == 1) {
                DesgarronLog.append("parseResponse(). wrong request error");
                return false;
            } else if (extractInt == 10) {
                DesgarronLog.append("parseResponse(). wrong request type error");
                return false;
            } else if (extractInt == 2) {
                DesgarronLog.append("parseResponse(). db error");
                return false;
            } else if (extractInt == 3) {
                DesgarronLog.append("parseResponse(). unknown terminal error");
                return false;
            } else if (extractInt == 4) {
                DesgarronLog.append("parseResponse(). unknown user error");
                return false;
            } else if (extractInt == 7) {
                DesgarronLog.append("parseResponse(). user password error");
                return false;
            } else if (extractInt == 5) {
                DesgarronLog.append("parseResponse(). user access error");
                return false;
            } else if (extractInt == 6) {
                DesgarronLog.append("parseResponse(). user flags error");
                return false;
            } else if (extractInt == 9) {
                DesgarronLog.append("parseResponse(). access policy error");
                return false;
            } else if (extractInt != 0) {
                DesgarronLog.append("parseResponse(). unknown error");
                return false;
            }
        }
            return true;
    }


    public static Emp2_0 onEMPAwaited(InPacket inPacket) {
        byte[] bArr;
        if (inPacket == null) {
            throw new IllegalArgumentException();
        } else if (inPacket.getType() != 2) {
            throw new IllegalArgumentException();
        } else if (inPacket.getCode() != 196) {
            throw new IllegalArgumentException();
        } else {
            int extractInt = inPacket.extractInt();
            Log.d("dogs",extractInt+""+" "+inPacket.getType()+" "+inPacket.getCode());
            if (extractInt == 11) {
                System.out.println("parseResponse(). licence violation error");
                return null;
            } else if (extractInt == 1) {
                System.out.println("parseResponse(). wrong request error");
                return null;
            } else if (extractInt == 10) {
                System.out.println("parseResponse(). wrong request type error");
                return null;
            } else if (extractInt == 2) {
                System.out.println("parseResponse(). db error");
                return null;
            } else if (extractInt == 3) {
                System.out.println("parseResponse(). unknown terminal error");
                return null;
            } else if (extractInt == 4) {
                System.out.println("parseResponse(). unknown user error");
                return null;
            } else if (extractInt == 5) {
                System.out.println("parseResponse(). user access error");
                return null;
            } else if (extractInt == 6) {
                System.out.println("parseResponse(). user flags error");
                return null;
            } else if (extractInt == 8) {
                Log.d("grimjow","parseResponse(). unknown card error");
                return Emp2_0.invalidEMP();
            } else if (extractInt == 9) {
                System.out.println("parseResponse(). access policy error");
                return null;
            } else if (extractInt != 0) {
                System.out.println("parseResponse(). unknown error");
                return null;
            } else {
                int extractInt2 = inPacket.extractInt();
                int extractInt3 = inPacket.extractInt();
                int extractInt4 = inPacket.extractInt();

                //Какой то треш
                //inPacket.extractInt();

                byte[] bArr2 = new byte[inPacket.extractInt()];
                inPacket.extractBytes(bArr2);
                int extractInt5 = inPacket.extractInt();
                if (extractInt5 > 0) {
                    bArr = new byte[extractInt5];
                    inPacket.extractBytes(bArr);
                } else {
                    bArr = null;
                }
                try {
                    EmpSigurBASE fromJson = EmpSigurBASE.fromJson(new JSONObject(new String(Crypto.decrypt(bArr2))));
                    byte[] decrypt = extractInt5 > 0 ? Crypto.decrypt(bArr) : null;
                    if (extractInt2 != Direction.N && extractInt2 != Direction.IN && extractInt2 != Direction.OUT) {
                    } else if (extractInt2 == Direction.IN && extractInt4 != 255) {
                    } else if (extractInt2 != Direction.OUT || extractInt3 == 255) {
                        //  System.out.println(extractInt4);
                        // System.out.println(extractInt3);


                        String[] emp1_0split = fromJson.getInfo().split("\n");
                        String empInfo="";
                        if(emp1_0split[3]!=null){
                            empInfo = buildEmpInfo(emp1_0split);
                        }
                        return new Emp2_0(fromJson.getId(),
                                emp1_0split[0],
                                emp1_0split[1],
                                emp1_0split[2],
                                empInfo
                                ,extractInt4,extractInt3,decrypt);
                        // System.out.println(fromJson.getId());
                        //return new RequestEmpTask.ResultEmp(new Personal.GetEmpResult(fromJson, -1), decrypt, extractInt2, extractInt2 == Direction.IN || extractInt2 == Direction.OUT, extractInt4, extractInt3);
                    } else {
                    }
                } catch (JSONException e) {
                    return null;
                }
                return null;
            }
        }
    }

    private static String buildEmpInfo(String[] empSplit){
        StringBuilder builder = new StringBuilder("");
       for(int i=3;i<empSplit.length;i++){
           builder.append(empSplit[i]+"\n");
           if(i==empSplit.length-1){
               builder.deleteCharAt(builder.length()-1);
           }
       }
       return builder.toString();
    }


}
