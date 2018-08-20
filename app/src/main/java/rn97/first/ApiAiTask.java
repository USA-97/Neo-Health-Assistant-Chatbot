package rn97.first;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonElement;
import java.util.Map;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class ApiAiTask extends AppCompatActivity implements AIListener{

    private AIService aiService;
    private Button listenButton;
    private Button sendButton;
    private EditText message;
    private LinearLayout ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_ai_task);

        listenButton = (Button) findViewById(R.id.listenButton);
        message = (EditText)findViewById(R.id.Message);
        sendButton = (Button) findViewById(R.id.sendButton);
        ll = (LinearLayout)findViewById(R.id.textBox);

        Intent intent = getIntent();
        if(intent!=null) {
            // Sending Score.
            int SCORE = Integer.parseInt(intent.getStringExtra("SCORE"));
            String query = Integer.toString(SCORE);
            //queryTextView.setText(query);
            final AIConfiguration config = new AIConfiguration("key",
                    AIConfiguration.SupportedLanguages.English,
                    AIConfiguration.RecognitionEngine.System);
            aiService = AIService.getService(this, config);
            aiService.setListener(this);
            final AIDataService aiDataService = new AIDataService(this,config);
            final AIRequest aiRequest = new AIRequest();
            aiRequest.setQuery(query);
            new AsyncTask<AIRequest, Void, AIResponse>() {
                @Override
                protected AIResponse doInBackground(AIRequest... requests) {
                    final AIRequest request = requests[0];
                    try {
                        final AIResponse response = aiDataService.request(aiRequest);
                        return response;
                    } catch (AIServiceException e) {
                    }
                    return null;
                }
                @Override
                protected void onPostExecute(AIResponse aiResponse) {
                    if (aiResponse != null) {
                        // process aiResponse here
                        onResult(aiResponse);
                    }
                }
            }.execute(aiRequest);

        }
    }
    public void listenButtonOnClick(final View view){
        aiService.startListening();
    }
    public void onResult(final AIResponse response) {
        Result result0 = response.getResult();
        // Get parameters
        String parameterString = "";
        if (result0.getParameters() != null && !result0.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result0.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }

        // Making new TextViews.
        LinearLayout ll = (LinearLayout) findViewById(R.id.textBox);
        TextView First = new TextView(this);
        TextView Second = new TextView(this);

        // Adding properties.

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.gravity = Gravity.LEFT;
        First.setLayoutParams(params1);
        First.setBackgroundColor(Color.parseColor("#03c4eb"));

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.gravity = Gravity.RIGHT;
        Second.setLayoutParams(params2);
        Second.setBackgroundColor(Color.parseColor("#03eb9e"));



        // Show results in TextView.
        /*
            First.setText("Query:" + result0.getResolvedQuery() +
                    "\nAction: " + result0.getAction() +
                    "\nParameters: " + parameterString);
        */
        First.setText(result0.getResolvedQuery());

        final Result result = response.getResult();
        final String speech = result.getFulfillment().getSpeech();
        //Log.i(TAG, "Speech: " + speech);
        Second.setText("Speech: "+speech);

        ll.addView(First);
        ll.addView(Second);

        message.setText(null);
    }

    public void sendButtonOnClick(View view){
        Special(0);
    }

    public void Special(int par){
        String query;
        if(par!=0) {
            query = Integer.toString(par);
        }else {
            query = message.getText().toString().trim();
            if(query.length()==0){
                Toast.makeText(this,"Enter Message",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        //Toast.makeText(ApiAiTask.this,"Q = "+query,Toast.LENGTH_LONG).show();
        //queryTextView.setText(query);

        final AIConfiguration config = new AIConfiguration("key",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        final AIDataService aiDataService = new AIDataService(this,config);
        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(query);
        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                //final AIRequest request = requests[0];
                try {
                    final AIResponse response = aiDataService.request(aiRequest);
                    return response;
                } catch (AIServiceException e) {

                }
                return null;
            }
            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                if (aiResponse != null) {
                    // process aiResponse here
                    onResult(aiResponse);
                }
            }
        }.execute(aiRequest);
    }

    @Override
    public void onError(final AIError error) {
        //resultTextView.setText(error.toString());
    }
    @Override
    public void onListeningStarted() {}

    @Override
    public void onListeningCanceled() {}

    @Override
    public void onListeningFinished() {}

    @Override
    public void onAudioLevel(final float level) {}




}
