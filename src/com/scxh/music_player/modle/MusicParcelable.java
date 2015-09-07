package com.scxh.music_player.modle;


import android.os.Parcel;
import android.os.Parcelable;

public class MusicParcelable implements Parcelable {
	private String MUSIC_NAME;
	private String MUSIC_SINGER;
	private String MUSIC_DURATION;
	private String MUSIC_PATH;
	
	public MusicParcelable() {
		super();
		// TODO Auto-generated constructor stub
	}


	public MusicParcelable(String mUSIC_NAME, String mUSIC_SINGER,
			String mUSIC_DURATION, String mUSIC_PATH) {
		super();
		MUSIC_NAME = mUSIC_NAME;
		MUSIC_SINGER = mUSIC_SINGER;
		MUSIC_DURATION = mUSIC_DURATION;
		MUSIC_PATH = mUSIC_PATH;
	}


	public String getMUSIC_NAME() {
		return MUSIC_NAME;
	}


	public void setMUSIC_NAME(String mUSIC_NAME) {
		MUSIC_NAME = mUSIC_NAME;
	}


	public String getMUSIC_SINGER() {
		return MUSIC_SINGER;
	}


	public void setMUSIC_SINGER(String mUSIC_SINGER) {
		MUSIC_SINGER = mUSIC_SINGER;
	}


	public String getMUSIC_DURATION() {
		return MUSIC_DURATION;
	}


	public void setMUSIC_DURATION(String mUSIC_DURATION) {
		MUSIC_DURATION = mUSIC_DURATION;
	}


	public String getMUSIC_PATH() {
		return MUSIC_PATH;
	}


	public void setMUSIC_PATH(String mUSIC_PATH) {
		MUSIC_PATH = mUSIC_PATH;
	}


	public static Parcelable.Creator<MusicParcelable> getCreator() {
		return CREATOR;
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(MUSIC_NAME);
		dest.writeString(MUSIC_SINGER);
		dest.writeString(MUSIC_DURATION);
		dest.writeString(MUSIC_PATH);
	}

	public static final Parcelable.Creator<MusicParcelable> CREATOR = new Parcelable.Creator<MusicParcelable>() {
		@Override
		public MusicParcelable createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			String name = source.readString();
			String singer = source.readString();
			String duration = source.readString();
			String tile = source.readString();
			MusicParcelable musicParcleable = new MusicParcelable(name, singer,
					duration, tile);
			return musicParcleable;
		}

		@Override
		public MusicParcelable[] newArray(int size) {
			// TODO Auto-generated method stub
			return new MusicParcelable[size];
		}
	};
}
