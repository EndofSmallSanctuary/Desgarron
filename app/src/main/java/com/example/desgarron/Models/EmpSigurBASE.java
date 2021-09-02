package com.example.desgarron.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class EmpSigurBASE {
    private static final String sCardIdKey = "card_id";
    private static final String sExptimeKey = "exptime";
    private static final String sExtCardsKey = "ext_cards";
    private static final SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private static final String sIdKey = "id";
    private static final String sInfoKey = "info";
    private static final String sPhotoVersionKey = "photo_version";
    private final Card mCard;
    private final List<Card> mExtCards;
    private final int mId;
    private final String mInfo;
    private final int mPhotoVersion;

    public static class Card {
        public final Date exptime;
        public final String id;

        private Card(String str, Date date) {
            this.id = str;
            this.exptime = date;
        }
    }

    public int getId() {
        return this.mId;
    }

    public String getInfo() {
        return this.mInfo;
    }

    /* access modifiers changed from: package-private */
    public int getPhotoVersion() {
        return this.mPhotoVersion;
    }

    public Card getCard() {
        return this.mCard;
    }

    public List<Card> getExtCards() {
        return this.mExtCards;
    }

    /* access modifiers changed from: package-private */
    public JSONObject toJson() {
        String str;
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(sIdKey, this.mId);
            jSONObject.put(sPhotoVersionKey, this.mPhotoVersion);
            jSONObject.put(sCardIdKey, this.mCard.id);
            jSONObject.put(sExptimeKey, this.mCard.exptime == null ? "" : sFormat.format(this.mCard.exptime));
            jSONObject.put(sInfoKey, this.mInfo);
            JSONArray jSONArray = new JSONArray();
            for (Card next : this.mExtCards) {
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put(sCardIdKey, next.id);
                if (next.exptime == null) {
                    str = "";
                } else {
                    str = sFormat.format(next.exptime);
                }
                jSONObject2.put(sExptimeKey, str);
                jSONArray.put(jSONObject2);
            }
            jSONObject.put(sExtCardsKey, jSONArray);
            return jSONObject;
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    public static EmpSigurBASE fromJson(JSONObject jSONObject) throws JSONException {
        JSONArray jSONArray;
        int i = jSONObject.getInt(sIdKey);
        int i2 = jSONObject.getInt(sPhotoVersionKey);
        String string = jSONObject.getString(sCardIdKey);
        String string2 = jSONObject.getString(sInfoKey);
        try {
            jSONArray = jSONObject.getJSONArray(sExtCardsKey);
        } catch (JSONException e) {
            jSONArray = null;
        }
        if (i < 1) {
            throw new JSONException("illegal id");
        } else if (i2 < 0) {
            throw new JSONException("illegal photo version");
        } else if (string == null) {
            throw new JSONException("illegal card id");
        } else if (string2 != null) {
            try {
                String string3 = jSONObject.getString(sExptimeKey);
                if (string3 != null) {
                    Card card = new Card(string, !string3.isEmpty() ? sFormat.parse(string3) : null);
                    LinkedList linkedList = new LinkedList();
                    if (jSONArray != null) {
                        int i3 = 0;
                        while (i3 < jSONArray.length()) {
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i3);
                            String string4 = jSONObject2.getString(sCardIdKey);
                            if (string4 != null) {
                                try {
                                    String string5 = jSONObject2.getString(sExptimeKey);
                                    if (string5 != null) {
                                        linkedList.add(new Card(string4, !string5.isEmpty() ? sFormat.parse(string5) : null));
                                        i3++;
                                    } else {
                                        throw new JSONException("illegal exptime");
                                    }
                                } catch (ParseException e2) {
                                    throw new JSONException("illegal exptime: " + e2.getMessage());
                                }
                            } else {
                                throw new JSONException("illegal ext card id");
                            }
                        }
                    }
                    return new EmpSigurBASE(i, i2, card, linkedList, string2);
                }
                throw new JSONException("illegal card exptime");
            } catch (ParseException e3) {
                throw new JSONException("illegal card exptime: " + e3.getMessage());
            }
        } else {
            throw new JSONException("illegal info");
        }
    }

    private EmpSigurBASE(int i, int i2, Card card, List<Card> list, String str) {
        this.mId = i;
        this.mPhotoVersion = i2;
        this.mCard = card;
        this.mExtCards = list;
        this.mInfo = str;
    }
}

