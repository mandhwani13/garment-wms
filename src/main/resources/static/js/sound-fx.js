/**
 * Warehouse Audio & Haptic Feedback Engine using Web Audio API
 * Generates instant acoustic feedback without needing external audio files.
 */
const SoundFX = (function () {
    let audioCtx = null;

    function getAudioContext() {
        if (!audioCtx) {
            const AudioContext = window.AudioContext || window.webkitAudioContext;
            if (AudioContext) {
                audioCtx = new AudioContext();
            }
        }
        if (audioCtx && audioCtx.state === 'suspended') {
            audioCtx.resume();
        }
        return audioCtx;
    }

    return {
        // High-pitched double chirp for scan success
        playSuccess: function () {
            try {
                const ctx = getAudioContext();
                if (!ctx) return;

                const now = ctx.currentTime;
                const osc = ctx.createOscillator();
                const gain = ctx.createGain();

                osc.type = 'sine';
                osc.frequency.setValueAtTime(1400, now);
                osc.frequency.exponentialRampToValueAtTime(1900, now + 0.08);

                gain.gain.setValueAtTime(0.3, now);
                gain.gain.exponentialRampToValueAtTime(0.01, now + 0.12);

                osc.connect(gain);
                gain.connect(ctx.destination);

                osc.start(now);
                osc.stop(now + 0.12);

                // Mobile haptic feedback
                if (navigator.vibrate) {
                    navigator.vibrate(80);
                }
            } catch (e) {
                console.warn("Audio playback error:", e);
            }
        },

        // Low buzz warning tone for validation failure
        playError: function () {
            try {
                const ctx = getAudioContext();
                if (!ctx) return;

                const now = ctx.currentTime;
                const osc = ctx.createOscillator();
                const gain = ctx.createGain();

                osc.type = 'sawtooth';
                osc.frequency.setValueAtTime(220, now);
                osc.frequency.linearRampToValueAtTime(180, now + 0.25);

                gain.gain.setValueAtTime(0.4, now);
                gain.gain.exponentialRampToValueAtTime(0.01, now + 0.25);

                osc.connect(gain);
                gain.connect(ctx.destination);

                osc.start(now);
                osc.stop(now + 0.25);

                // Mobile haptic error alert
                if (navigator.vibrate) {
                    navigator.vibrate([150, 80, 150]);
                }
            } catch (e) {
                console.warn("Audio playback error:", e);
            }
        }
    };
})();
