package com.mobao.watch.activity;

import android.app.Activity;
import android.os.Bundle;

import com.mb.zjwb1.R;
import com.mobao.watch.view.RoundProgressView;
import com.testin.agent.TestinAgent;

public class RewardFragmentActivity extends Activity {

	private RoundProgressView round;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//云测崩溃分析
		TestinAgent.init(this);
		setContentView(R.layout.reward_fragment);

		round = (RoundProgressView) findViewById(R.id.reward_round_progress);
		round.setmPercent(0f);
	}

}
