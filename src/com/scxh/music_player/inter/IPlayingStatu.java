package com.scxh.music_player.inter;

public interface IPlayingStatu {
	int FIRST_PLAYING=0; //������һ�β���ʱ��״̬
	int ON_PLAYING = 1; //�������ڲ��ŵ�״̬
	int ON_PAUSE = 2;  //������ͣ��״̬
	int SINGLE_PLAYING = 0; //��������ѭ��
	int LOOPING_PLAYING = 1; //�����Զ�ѭ��
	int RANDOM_PLAYING = 2; //�����������
}
