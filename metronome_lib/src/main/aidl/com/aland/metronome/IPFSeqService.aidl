// IPFSeqService.aidl
package com.aland.metronome;

// Declare any non-default types here with import statements
import com.aland.metronome.IServiceCallback;

parcelable PFSeqConfig;

interface IPFSeqService {

boolean init(String file, in PFSeqConfig config);

void play();
boolean isPlaying();
void stop();

void setBmp(double newBmp);
double getBmp();


void registerListener(IServiceCallback listener);
}
