package wang.julis.jproject.example.little;

import wang.julis.jproject.example.binder.client.BinderTestActivity;
import wang.julis.jwbase.basecompact.baseList.BaseListActivity;

/*******************************************************
 *
 * Created by juliswang on 2022/02/09 14:27 
 *
 * Description : 
 *
 *
 *******************************************************/

public class LittleMainActivity extends BaseListActivity {
    @Override
    protected void initData() {
        addActivity("JsonTest", JsonTestActivity.class);
        addActivity("BinderExample", BinderTestActivity.class);
        addActivity("文字编码", CharacterDecodingActivity.class);
        addActivity("Router", RouterActivity.class);
    }
}
