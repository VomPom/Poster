package julis.wang.learnopengl.opengl;

import static android.opengl.GLSurfaceView.RENDERMODE_CONTINUOUSLY;
import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;
import static julis.wang.learnopengl.opengl.MyGLSurfaceView.IMAGE_FORMAT_NV21;
import static julis.wang.learnopengl.opengl.MyGLSurfaceView.IMAGE_FORMAT_RGBA;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_BASIC_LIGHTING;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_BLENDING;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_COORD_SYSTEM;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_DEPTH_TESTING;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_EGL;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_FBO;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_INSTANCING;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_KEY_TBO;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_KEY_TEXT_RENDER;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_KEY_TRANSITIONS_1;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_KEY_TRANSITIONS_2;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_KEY_TRANSITIONS_3;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_KEY_TRANSITIONS_4;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_MULTI_LIGHTS;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_PARTICLES;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_PBO;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_SKYBOX;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_STENCIL_TESTING;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_TEXTURE_MAP;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_TRANS_FEEDBACK;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_TRIANGLE;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_VAO;
import static julis.wang.learnopengl.opengl.MyNativeRender.SAMPLE_TYPE_YUV_TEXTURE_MAP;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import julis.wang.learnopengl.R;
import julis.wang.learnopengl.opengl.egl.EGLActivity;
import julis.wang.learnopengl.opengl.glitem.GLItemListAdapter;
import julis.wang.learnopengl.opengl.glitem.GLItemListModel;
import wang.julis.jwbase.basecompact.BaseActivity;


/*******************************************************
 *
 * Created by julis.wang on 2022/02/09 15:27
 *
 * Description : NDK 实现OpenGL ES相关效果
 *
 *
 * History   :
 *
 *******************************************************/

public class OpenGLNDKListActivity extends BaseActivity {

    private MyGLSurfaceView mGLSurfaceView;
    private FrameLayout mContainer;
    protected GLItemListAdapter mAdapter;
    private ImageView ivClose;
    protected final List<GLItemListModel> mDataList = new ArrayList<>();

    private int index = 0;

    private final MyGLRender mGLRender = new MyGLRender();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLRender.init();
        mAdapter.updateData(mDataList);
    }


    @Override
    protected void initView() {
        mContainer = findViewById(R.id.fl_surface_container);
        RecyclerView rvList = findViewById(R.id.rv_list);

        mAdapter = new GLItemListAdapter(this);
        rvList.setAdapter(mAdapter);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        ivClose = findViewById(R.id.iv_close);
        ivClose.setOnClickListener(v -> {
            mContainer.removeView(mGLSurfaceView);
            ivClose.setVisibility(View.GONE);
        });

        mAdapter.setItemListener(index -> {
            ivClose.setVisibility(View.VISIBLE);
            onRenderChange(index);
        });
    }

    private void onRenderChange(int index) {
        int SAMPLE_TYPE = 200;
        int sampleType = index + SAMPLE_TYPE;
        mGLRender.setParamsInt(SAMPLE_TYPE, index + SAMPLE_TYPE, 0);

        mGLSurfaceView = new MyGLSurfaceView(this, mGLRender);
        mGLSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);

        mContainer.addView(mGLSurfaceView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Bitmap tmp;
        switch (sampleType) {
            case SAMPLE_TYPE_TRIANGLE:
            case SAMPLE_TYPE_VAO:
                break;
            case SAMPLE_TYPE_TEXTURE_MAP:
            case SAMPLE_TYPE_FBO:
            case SAMPLE_TYPE_YUV_TEXTURE_MAP:
                loadNV21Image();
                break;
            case SAMPLE_TYPE_COORD_SYSTEM:
            case SAMPLE_TYPE_BASIC_LIGHTING:
            case SAMPLE_TYPE_TRANS_FEEDBACK:
            case SAMPLE_TYPE_MULTI_LIGHTS:
            case SAMPLE_TYPE_DEPTH_TESTING:
            case SAMPLE_TYPE_INSTANCING:
            case SAMPLE_TYPE_STENCIL_TESTING:
                loadRGBAImage(R.drawable.board_texture);
                break;
            case SAMPLE_TYPE_KEY_TEXT_RENDER:
                Bitmap b3 = loadRGBAImage(R.drawable.person);
                mGLSurfaceView.setAspectRatio(b3.getWidth(), b3.getHeight());
                mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                break;
            case SAMPLE_TYPE_KEY_TBO:
                Bitmap b4 = loadRGBAImage(R.drawable.person);
                mGLSurfaceView.setAspectRatio(b4.getWidth(), b4.getHeight());
                break;
            case SAMPLE_TYPE_BLENDING:
                loadRGBAImage(R.drawable.board_texture, 0);
                loadRGBAImage(R.drawable.floor, 1);
                loadRGBAImage(R.drawable.window, 2);
                break;
            case SAMPLE_TYPE_PARTICLES:
                loadRGBAImage(R.drawable.board_texture);
                mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                break;
            case SAMPLE_TYPE_SKYBOX:
                loadRGBAImage(R.drawable.right, 0);
                loadRGBAImage(R.drawable.left, 1);
                loadRGBAImage(R.drawable.top, 2);
                loadRGBAImage(R.drawable.bottom, 3);
                loadRGBAImage(R.drawable.back, 4);
                loadRGBAImage(R.drawable.front, 5);
                break;
            case SAMPLE_TYPE_PBO:
                loadRGBAImage(R.drawable.front);
                mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                break;
            case SAMPLE_TYPE_EGL:
                ivClose.performClick();
                startActivity(new Intent(this, EGLActivity.class));
                break;
            case SAMPLE_TYPE_KEY_TRANSITIONS_1:
            case SAMPLE_TYPE_KEY_TRANSITIONS_2:
            case SAMPLE_TYPE_KEY_TRANSITIONS_3:
            case SAMPLE_TYPE_KEY_TRANSITIONS_4:
                loadRGBAImage(R.drawable.lye, 0);
                loadRGBAImage(R.drawable.lye4, 1);
                loadRGBAImage(R.drawable.lye5, 2);
                loadRGBAImage(R.drawable.lye6, 3);
                loadRGBAImage(R.drawable.lye7, 4);
                tmp = loadRGBAImage(R.drawable.lye8, 5);
                mGLSurfaceView.setAspectRatio(tmp.getWidth(), tmp.getHeight());
                mGLSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
                break;
            default:
        }

        mGLSurfaceView.requestRender();
    }

    @Override
    protected void initData() {
        Arrays.stream(SAMPLE_TITLES).forEach(this::addItem);
    }

    protected void addItem(String activityName) {
        mDataList.add(new GLItemListModel(activityName, index++));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_opengl_list;
    }

    private Bitmap loadRGBAImage(int resId) {
        return loadRGBAImage(resId, -1);
    }

    private Bitmap loadRGBAImage(int resId, int index) {
        InputStream is = this.getResources().openRawResource(resId);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null) {
                int bytes = bitmap.getByteCount();
                ByteBuffer buf = ByteBuffer.allocate(bytes);
                bitmap.copyPixelsToBuffer(buf);
                byte[] byteArray = buf.array();
                if (index == -1) {
                    mGLRender.setImageData(IMAGE_FORMAT_RGBA, bitmap.getWidth(), bitmap.getHeight(), byteArray);
                } else {
                    mGLRender.setImageDataWithIndex(index, IMAGE_FORMAT_RGBA, bitmap.getWidth(), bitmap.getHeight(), byteArray);
                }
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    @Override
    public void onBackPressed() {
        if (mContainer.getChildCount() != 0) {
            mContainer.removeView(mGLSurfaceView);
            ivClose.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void loadNV21Image() {
        InputStream is = null;
        try {
            is = getAssets().open("YUV_Image_840x1074.NV21");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int lenght = 0;
        try {
            lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            mGLRender.setImageData(IMAGE_FORMAT_NV21, 840, 1074, buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static final String[] SAMPLE_TITLES = {
            "基础三角形",
            "纹理映射",
            "YUV渲染",
            "VAO&VBO",
            "FBO离屏渲染",
            "EGL后台渲染",
            "文字渲染",
            "坐标系统",
            "变换反馈",
            "基础光照",
            "复杂光照",
            "深度测试",
            "实例化",
            "模板测试",
            "混合",
            "粒子",
            "立方体贴图",
            "Assimp 加载3D模型",
            "PBO离屏渲染",
            "TBO缓存纹理",
            "转场-翻页"

    };

}
