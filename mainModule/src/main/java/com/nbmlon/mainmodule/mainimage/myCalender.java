package com.nbmlon.mainmodule.mainimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.nbmlon.mainmodule.R;
import com.nbmlon.managemodule.SharedPreferenceManager;

import org.joda.time.DateTime;

public abstract class myCalender {

    protected SharedPreferenceManager.BitmapLoader bitmapLoader;
    protected SharedPreferenceManager.MarkersLoader markersLoader;


    protected static final int NOT_DAY = 100;
    protected static final int WIN_DAY = 101;
    protected static final int DRW_DAY = 102;
    protected static final int LOS_DAY = 103;

    protected static final int col = 7;
    protected static final int row = 6;

    protected static final Paint mDay_paint = new Paint();
    protected static final Paint mEpt_paint = new Paint();
    protected static Paint DFT_Paint, DFT_TextPaint, TODAY_Paint, Today_TextPaint, UnderBarPaint_Today;
    protected static Paint WIN_Paint, DRW_Paint, LOS_Paint ;
    protected static float UnderbarHeight, textSize;
    protected int mDstDayStart;


    protected Bitmap mResBitmap;
    protected Bitmap imgPic[][] = new Bitmap[row][col];
    protected int width, height, picW, picH, gap;

    protected DateTime mDstDate;
    protected int MAX_DAYOFMONTH;
    protected int WIN_MARKER, DRW_MARKER, TRY_MARKER;




    public static void CalenderSetting(int winColor, int drwColor, int losColor, int todayColor){
        DFT_Paint = new Paint();
        DFT_Paint.setColor(Color.parseColor("#AA000000"));

        WIN_Paint = new Paint();          WIN_Paint.setColor(winColor);
        DRW_Paint = new Paint();          DRW_Paint.setColor(drwColor);
        LOS_Paint = new Paint();          LOS_Paint.setColor(losColor);

        TODAY_Paint = new Paint();           TODAY_Paint.setColor(todayColor);
        UnderBarPaint_Today = new Paint();   UnderBarPaint_Today.setColor(Color.RED);

        DFT_TextPaint = new Paint();            DFT_TextPaint.setColor(Color.WHITE);
        DFT_TextPaint.setTextAlign(Paint.Align.CENTER);

        Today_TextPaint = new Paint(DFT_TextPaint);   Today_TextPaint.setColor(Color.RED);

        mDay_paint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.INNER));
        mEpt_paint.setMaskFilter(new BlurMaskFilter(80, BlurMaskFilter.Blur.INNER));

    }

    protected void setBasedImg(){
        Bitmap bm = bitmapLoader.getBitmap( R.drawable.df_mainimage);
        setImgPic(bm);
    }

    protected void setImgPic(Bitmap bm) {
        Bitmap imgOrg = Bitmap.createScaledBitmap(bm, width, height, true);
        for (int i = 0; i < row; i++)
            for (int j = 0; j < col; j++)
                imgPic[i][j] = Bitmap.createBitmap(imgOrg, j * picW + gap, i * picH + gap, picW - 2 * gap, picH - 2 * gap);
    }


    protected void draw(){
    }






    /** Current Calender with Today Marking **/
    public static class curCalender extends myCalender {
        private static final int TODAY = 104;
        private static final int DAY_PLANED = 105;
        private int today;


        public curCalender(Context context, int size) {
            mDstDate = new DateTime();
            today = mDstDate.getDayOfMonth();
            MAX_DAYOFMONTH = mDstDate.toLocalDate().dayOfMonth().withMaximumValue().getDayOfMonth();

            width = size;                   height = size;
            picW = size / col;             picH = size / row;
            gap = picW / 20;
            textSize = picW/5;              UnderbarHeight = picW/21;

            DFT_TextPaint.setTextSize(textSize);
            Today_TextPaint.setTextSize(textSize);


            int dstyyyyMM = Integer.parseInt(mDstDate.toString("yyyyMM"));
            bitmapLoader = new SharedPreferenceManager.BitmapLoader(context, dstyyyyMM);
            markersLoader = new SharedPreferenceManager.MarkersLoader(context, dstyyyyMM);
            setBasedImg();
            setMarker();
            draw();
        }

        public Bitmap getCalender(){
            if (setMarker())
                draw();
            return mResBitmap;
        }

        public void ChangeBasedImg(Bitmap bm){
            bitmapLoader.saveBitmap(bm);
            setImgPic(bm);
            draw();
        }


        @Override
        protected void draw(){
            mResBitmap = Bitmap.createBitmap(width, height, Bitmap.Config. ARGB_8888);
            Canvas canvas = new Canvas(mResBitmap);
            canvas.drawColor(Color.BLACK);

            mDstDayStart = -(mDstDate.withDayOfMonth(1).getDayOfWeek() - 1);

            for (int i = 0; i < row; i++){
                try {
                    Thread tmpThread = getThread(canvas, i);
                    tmpThread.start();
                    tmpThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        private Thread getThread(Canvas canvas, int row){
            return new Thread(() -> {
                int day_count = mDstDayStart + row * col;
                for (int j = 0; j < col; j++) {
                    int i = row;
                    switch (CheckDay(day_count)){
                        case NOT_DAY :
                            canvas.drawBitmap(imgPic[i][j], j * picW + gap, i * picH + gap, mEpt_paint);
                            break;
                        case WIN_DAY :
                            canvas.drawBitmap(imgPic[i][j], j * picW + gap, i * picH + gap, mDay_paint);
                            canvas.drawRect(new RectF(j* picW + gap, i* picH + gap , j* picW +picW/3, i * picH + picW / 3),  WIN_Paint);
                            canvas.drawText(Integer.toString(day_count), j*picW + gap*4, i * picH + gap + textSize, DFT_TextPaint);
                            break;
                        case DRW_DAY :
                            canvas.drawBitmap(imgPic[i][j], j * picW + gap, i * picH + gap, mDay_paint);
                            canvas.drawRect(new RectF(j* picW + gap, i* picH + gap , j* picW +picW/3, i * picH + picW / 3),  DRW_Paint);
                            canvas.drawText(Integer.toString(day_count), j*picW + gap*4, i * picH + gap + textSize, DFT_TextPaint);
                            break;
                        case LOS_DAY :
                            canvas.drawBitmap(imgPic[i][j], j * picW + gap, i * picH + gap, mDay_paint);
                            canvas.drawRect(new RectF(j* picW + gap, i* picH + gap , j* picW +picW/3, i * picH + picW / 3),  LOS_Paint);
                            canvas.drawText(Integer.toString(day_count), j*picW + gap*4, i * picH + gap + textSize, DFT_TextPaint);
                            break;
                        case TODAY  :
                            canvas.drawBitmap(imgPic[i][j], j * picW + gap, i * picH + gap, mDay_paint);
                            canvas.drawRect(new RectF(j* picW + gap, i* picH + gap , j* picW +picW/3, i * picH + picW / 3),  TODAY_Paint);
                            canvas.drawRect(new RectF(j* picW + gap, i * picH + picW / 3 - UnderbarHeight , j* picW +picW/3, i * picH + picW / 3),  UnderBarPaint_Today);
                            canvas.drawText(Integer.toString(day_count), j*picW + gap*4, i * picH + gap + textSize, Today_TextPaint);
                            break;
                        case DAY_PLANED:
                            canvas.drawBitmap(imgPic[i][j], j * picW + gap, i * picH + gap, mDay_paint);
                            canvas.drawRect(new RectF(j* picW + gap, i* picH + gap , j* picW +picW/3, i * picH + picW / 3),  DFT_Paint);
                            canvas.drawText(Integer.toString(day_count), j*picW + gap*4, i * picH + gap + textSize, DFT_TextPaint);
                            canvas.drawRect(new RectF(j* picW + gap, i * picH + picW / 3 + gap , j* picW +picW/3, i * picH + picW / 3 + gap + UnderbarHeight),  DFT_Paint);
                            break;
                        default:
                            canvas.drawBitmap(imgPic[i][j], j * picW + gap, i * picH + gap, mDay_paint);
                            canvas.drawRect(new RectF(j* picW + gap, i* picH + gap , j* picW +picW/3, i * picH + picW / 3),  DFT_Paint);
                            canvas.drawText(Integer.toString(day_count), j*picW + gap*4, i * picH + gap + textSize, DFT_TextPaint);
                            break;
                    }
                    day_count++;
                }
            });
        }


        private int CheckDay(int day_count){
            if ( day_count < 1 || day_count > MAX_DAYOFMONTH)
                return NOT_DAY;
            else {
                int ret = -1;
                int DST_MARKER = 0b1;
                DST_MARKER <<= day_count-1;
                if (day_count < today){
                    if ((DST_MARKER & WIN_MARKER) > 0)
                        ret = WIN_DAY;
                    else if ((DST_MARKER & DRW_MARKER) > 0)
                        ret = DRW_DAY;
                    else if ((DST_MARKER & TRY_MARKER) > 0)
                        ret = LOS_DAY;
                    else
                        ret = -1;
                }
                else if (day_count > today){
                    if ((DST_MARKER & TRY_MARKER) > 0)
                        ret = DAY_PLANED;
                    else
                        ret = -1;
                }
                else
                    ret = TODAY;

                return ret;
            }
        }

        private boolean setMarker(){
            int[] res = markersLoader.getMarkers();
            if(
                    TRY_MARKER != res[markersLoader.TRY_MARKER_INDEX] ||
                            DRW_MARKER != res[markersLoader.DRW_MARKER_INDEX] ||
                            WIN_MARKER != res[markersLoader.WIN_MARKER_INDEX]
            ){
                TRY_MARKER = res[markersLoader.TRY_MARKER_INDEX];
                DRW_MARKER = res[markersLoader.DRW_MARKER_INDEX];
                WIN_MARKER = res[markersLoader.WIN_MARKER_INDEX];

                return true;
            }
            return false;
        }
    }



















    /**
     * Calender For get Result
     * (No Todays Marking)
     * **/
    public static class resCalender extends myCalender {

        public static final int DAY_COUNT = 0;
        public static final int WIN_COUNT = 1;
        public static final int DRW_COUNT = 2;
        public static final int LOS_COUNT = 3;


        private int[] Summary;

        public resCalender(Context context, int size, int year, int month) {
            mDstDate = new DateTime().withDate(year, month, 1);
            MAX_DAYOFMONTH = mDstDate.toLocalDate().dayOfMonth().withMaximumValue().getDayOfMonth();

            width = size;                   height = size;
            picW = size / col;             picH = size / row;
            gap = picW / 20;
            textSize = picW/5;              UnderbarHeight = picW/21;

            DFT_TextPaint.setTextSize(textSize);
            Today_TextPaint.setTextSize(textSize);


            int dstyyyyMM = Integer.parseInt(mDstDate.toString("yyyyMM"));
            bitmapLoader = new SharedPreferenceManager.BitmapLoader(context, dstyyyyMM);
            markersLoader = new SharedPreferenceManager.MarkersLoader(context, dstyyyyMM);

            setMarker();
            setBasedImg();
            draw();
        }

        public Bitmap getCalender(){
            return mResBitmap;
        }

        public int[] getSummary(){
            return Summary;
        }

        @Override
        protected void draw(){
            Summary = new int[] {0,0,0,0};
            mResBitmap = Bitmap.createBitmap(width, height, Bitmap.Config. ARGB_8888);
            Canvas canvas = new Canvas(mResBitmap);
            canvas.drawColor(Color.BLACK);

            mDstDayStart = -(mDstDate.withDayOfMonth(1).getDayOfWeek() - 1);

            for (int i = 0; i < row; i++){
                try {
                    Thread tmpThread = getThread(canvas, i);
                    tmpThread.start();
                    tmpThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        private Thread getThread(Canvas canvas, int row){
            return new Thread(() -> {
                int day_count = mDstDayStart + row * col;
                for (int j = 0; j < col; j++) {
                    int i = row;
                    switch (CheckDay(day_count)){
                        case NOT_DAY :
                            canvas.drawBitmap(imgPic[i][j], j * picW + gap, i * picH + gap, mEpt_paint);
                            break;
                        case WIN_DAY :
                            canvas.drawBitmap(imgPic[i][j], j * picW + gap, i * picH + gap, mDay_paint);
                            canvas.drawRect(new RectF(j* picW + gap, i* picH + gap , j* picW +picW/3, i * picH + picW / 3),  WIN_Paint);
                            canvas.drawText(Integer.toString(day_count), j*picW + gap*4, i * picH + gap + textSize, DFT_TextPaint);
                            break;
                        case DRW_DAY :
                            canvas.drawBitmap(imgPic[i][j], j * picW + gap, i * picH + gap, mDay_paint);
                            canvas.drawRect(new RectF(j* picW + gap, i* picH + gap , j* picW +picW/3, i * picH + picW / 3),  DRW_Paint);
                            canvas.drawText(Integer.toString(day_count), j*picW + gap*4, i * picH + gap + textSize, DFT_TextPaint);
                            break;
                        case LOS_DAY :
                            canvas.drawBitmap(imgPic[i][j], j * picW + gap, i * picH + gap, mDay_paint);
                            canvas.drawRect(new RectF(j* picW + gap, i* picH + gap , j* picW +picW/3, i * picH + picW / 3),  LOS_Paint);
                            canvas.drawText(Integer.toString(day_count), j*picW + gap*4, i * picH + gap + textSize, DFT_TextPaint);
                            break;
                        default:
                            canvas.drawBitmap(imgPic[i][j], j * picW + gap, i * picH + gap, mDay_paint);
                            canvas.drawRect(new RectF(j* picW + gap, i* picH + gap , j* picW +picW/3, i * picH + picW / 3),  DFT_Paint);
                            canvas.drawText(Integer.toString(day_count), j*picW + gap*4, i * picH + gap + textSize, DFT_TextPaint);
                            break;
                    }

                    day_count++;
                }
            });
        }


        private int CheckDay(int day_count){
            if ( day_count < 1 || day_count > MAX_DAYOFMONTH)
                return NOT_DAY;
            else {
                Summary[DAY_COUNT]++;
                int DST_MARKER = 0b1;
                DST_MARKER <<= day_count-1;
                int ret;
                if ((DST_MARKER & WIN_MARKER) > 0){
                    ret = WIN_DAY;
                    Summary[WIN_COUNT]++;
                }
                else if ((DST_MARKER & DRW_MARKER) > 0){
                    ret = DRW_DAY;
                    Summary[DRW_COUNT]++;

                }
                else if ((DST_MARKER & TRY_MARKER) > 0) {
                    ret = LOS_DAY;
                    Summary[LOS_COUNT]++;
                }
                else
                    ret = -1;

                return ret;
            }
        }

        private void setMarker(){
            int[] res = markersLoader.getMarkers();
            TRY_MARKER = res[markersLoader.TRY_MARKER_INDEX];
            DRW_MARKER = res[markersLoader.DRW_MARKER_INDEX];
            WIN_MARKER = res[markersLoader.WIN_MARKER_INDEX];
        }
    }

}
