package team.mars.marssenger.main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import team.mars.marssenger.R;
import team.mars.marssenger.datatype.Chat;


public class MainActivity extends ActionBarActivity implements
            Toolbar.OnMenuItemClickListener,
            MainFragment.mainFragmentCallbacks
{

    //layout-attr

    private Toolbar toolbar;

    //mvc
    private MainInteractor mainInteractor;

    @Override
    protected void onResume() {
        super.onResume();
    }

    //attr
    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT ==Build.VERSION_CODES.LOLLIPOP) {
            colorActionBar();
        }

        setContentView(R.layout.activity_main);

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        if (toolbar!=null){

            setSupportActionBar(toolbar);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

            toolbar.setTitle(R.string.app_name);

            //menu
            toolbar.inflateMenu(R.menu.menu_main);
            toolbar.setOnMenuItemClickListener(this);
        } else {
            test("toolbar null");
        }

        MainFragment mainFragment=MainFragment.getInstance(this, this);//context and callbacks

        MainInteractor mainInteractor=new MainInteractorImpl(this);

        mainPresenter=new MainPresenterImpl(mainFragment,mainInteractor,this);

        replaceContainer(mainFragment,true);

    }

    public void replaceContainer(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction=getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        if (addToBackStack){
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void colorActionBar() {getWindow().setStatusBarColor(getResources().getColor(R.color.primary700));}

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return mainPresenter.menuItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void test(CharSequence charSequence) {
        Toast.makeText(this, charSequence, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mainPresenter.onRegsiterReturn(requestCode,resultCode,data);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        test("click");
        return mainPresenter.menuItemSelected(menuItem);
    }

    @Override
    public void openChat(Chat chat) {
        //TODO open Chat in ChatFragment
    }
}
