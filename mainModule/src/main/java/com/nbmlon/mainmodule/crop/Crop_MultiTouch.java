package com.nbmlon.mainmodule.crop;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


/*
https://stackoverflow.com/questions/33093346/rotate-zoom-scale-drag-bitmap-with-ontouchevent-android
 */


public class Crop_MultiTouch extends Activity implements View.OnTouchListener {

        private static Matrix start_matrix = new Matrix();

        // these matrices will be used to move and zoom image
        private static Matrix matrix = new Matrix();
        private static Matrix savedMatrix = new Matrix();

        // we can be in one of these 3 states
        private static final int NONE = 0;
        private static final int DRAG = 1;
        private static final int ZOOM = 2;

        private static int mode = NONE;

        // remember some things for zooming
        private static PointF start = new PointF();
        private static PointF mid = new PointF();
        private static float oldDist = 1f;
        private static float[] startf = new float[9];
        private static float[] oldf = new float[9];
        private static float[] f = new float[9];

        private static ImageView mImageView;
        private static BoundaryForImageView mImageViewRect;
        private static RectF mImageRect;
        private static RectF mOrgImageRect;

        /** ImageView의 Rect 저장 **/
        public static class BoundaryForImageView {
                public float left = 0, right = 0, top = 0, bottom =0;
                BoundaryForImageView(ImageView iv){
                    right = iv.getWidth();
                    bottom = iv.getHeight();
                }
        }

        /** Scaled된 Matrix에 맞게 Image Rect 변환**/
        public static void UpdateImageRect(){
            matrix.getValues(f);
            mImageRect.right = mOrgImageRect.right * f[Matrix.MSCALE_X];
            mImageRect.bottom = mOrgImageRect.bottom * f[Matrix.MSCALE_Y];
        }


        public Crop_MultiTouch(ImageView v){
            mImageView = v;
            matrix = new Matrix(mImageView.getImageMatrix());

            start_matrix = new Matrix(matrix);
            start_matrix.getValues(startf);

            mImageViewRect = new BoundaryForImageView(v);
            mOrgImageRect = new RectF(mImageView.getDrawable().copyBounds());
            mImageRect = new RectF(mOrgImageRect);
        }

        public void Resized(){
            matrix = new Matrix(mImageView.getImageMatrix());
            start_matrix = new Matrix(matrix);
            start_matrix.getValues(startf);
            UpdateImageRect();
        }





        @Override
        public boolean onTouch(View v, MotionEvent event) {

        // handle touch events here
            ImageView view = (ImageView) v;

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        start.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        matrix.getValues(oldf);
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        mode = NONE;
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        UpdateImageRect();
                        ResizeWithinBoundary();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            float dx = event.getX() - start.x;
                            float dy = event.getY() - start.y;

                            savedMatrix.getValues(f);
                            float totalDx = f[Matrix.MTRANS_X] + dx;
                            float totalDy = f[Matrix.MTRANS_Y] + dy;

                            // Out Of Range
                            if ( mImageRect.left + totalDx > mImageViewRect.left ||
                                    mImageRect.right + totalDx  < mImageViewRect.right)
                                dx = 0;


                            if (mImageRect.top + totalDy > mImageViewRect.top ||
                                    mImageRect.bottom + totalDy < mImageViewRect.bottom)
                                dy = 0;


                            matrix.postTranslate(dx, dy);
                            savedMatrix.set(matrix);
                            start.set(event.getX(),event.getY());
                        }


                        else if (mode == ZOOM) {

                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = (newDist / oldDist);
                                matrix.postScale(scale, scale, mid.x, mid.y);
                            }
                        }
                        break;

                    default:
                        break;
                }
                view.setImageMatrix(matrix);
                return true;
        }



        /**
         * Determine the space between the first two fingers
         */

        private float spacing(MotionEvent event) {

            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);

        }

        /**
         * Calculate the mid point of the first two fingers
         */

        private void midPoint(PointF point, MotionEvent event) {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        }



    /**
     * if image Scaled Out Of Range
     * Scaling within Boundary with animation
     */

    private void ResizeWithinBoundary(){
        float dx = f[Matrix.MTRANS_X], dy = f[Matrix.MTRANS_Y];

        boolean UNFIT_WIDTH = mImageRect.right < mImageViewRect.right ||
                                mImageRect.left + dx > mImageViewRect.left ||
                                mImageRect.right + dx < mImageViewRect.right;
        boolean UNFIT_HEIGHT = mImageRect.bottom < mImageViewRect.bottom||
                                mImageRect.top + dy > mImageViewRect.top ||
                                mImageRect.bottom + dy < mImageViewRect.bottom;

        boolean OVERFIT_WIDTH = mImageRect.right > mImageViewRect.right * 4;
        boolean OVERFIT_HEIGHT =  mImageRect.bottom > mImageViewRect.bottom * 4;

        /**Maximum Zoom out */
        if (UNFIT_WIDTH || UNFIT_HEIGHT) {
            float oldx = oldf[Matrix.MTRANS_X]/oldf[Matrix.MSCALE_X] * startf[Matrix.MSCALE_X]
                    , oldy = oldf[Matrix.MTRANS_Y] /oldf[Matrix.MSCALE_Y] * startf[Matrix.MTRANS_Y];

            matrix.set(start_matrix);
            UpdateImageRect();


            // Width > Height -> Align Height
            if (mImageRect.right > mImageRect.bottom){
                //Image Fit LeftEnd
                if(mImageRect.left + oldx > mImageViewRect.left)
                    matrix.postTranslate(0,0);
                //Image Fit RightEnd
                else if(mImageRect.right + oldx < mImageViewRect.right)
                    matrix.postTranslate(mImageViewRect.right - mImageRect.right,0);
                else
                    matrix.postTranslate(oldx, 0);

            }

            // Height > Width -> Align Width
            else{
                //Image Fit TopEnd
                if(mImageRect.top + oldy < mImageViewRect.top)
                    matrix.postTranslate(0,0);
                //Image Fit BottomEnd
                else if(mImageRect.bottom + oldy > mImageViewRect.bottom)
                {
                    matrix.postTranslate(0, mImageViewRect.bottom - mImageRect.bottom);
                }

                else
                    matrix.postTranslate(0, oldy);
            }


            mImageView.setImageMatrix(matrix);
        }

        // 모서리에 줌 아웃 했을 떄 처리

        /** Maximum Zoom in    */
        else if (OVERFIT_WIDTH || OVERFIT_HEIGHT) {
            matrix.postScale(4/f[Matrix.MSCALE_X] *startf[Matrix.MSCALE_X] , 4/f[Matrix.MSCALE_Y] * startf[Matrix.MSCALE_Y], mid.x, mid.y);
            mImageView.setImageMatrix(matrix);
            UpdateImageRect();
        }


    }
}
