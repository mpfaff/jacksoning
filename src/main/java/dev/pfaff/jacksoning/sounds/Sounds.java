package dev.pfaff.jacksoning.sounds;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class Sounds {
	public static final SoundEvent OFF_THE_WALL_LOOP = register("offthewallloop");
	public static final SoundEvent OFF_THE_WALL_START = register("offthewallstart");
	public static final SoundEvent INTRO = register("jacksoningintro");
	public static final SoundEvent SAINT_PEPSI = register("saintpepsi");
	public static final SoundEvent GET_ON_THE_FLOOR_REVERB = register("getonthefloorreverb");
	public static final SoundEvent ROCK_WITH_YOU_SHORT = register("rockwithyoushort");
	public static final SoundEvent HEE_HEE = register("heehee");
	public static final SoundEvent IDLE = register("jacksonidle");
	public static final SoundEvent SUPER_CBT = register("supercbt");
	public static final SoundEvent CBT_CAST = register("cbtcast");
	public static final SoundEvent ONE_AND_ONLY = register("oneandonly");
	public static final SoundEvent BOOGIE_SCAN = register("boogiescan");
	public static final SoundEvent UNWINS = register("unwins");
	public static final SoundEvent OH_MY_GOD_WERE_DOOMED = register("ohmygodweredoomed");
	public static final SoundEvent MJ_WINS = register("mjwins");
	public static final SoundEvent PSYAMI_START = register("psyamistart");
	public static final SoundEvent PSYAMI_LOOP = register("psyamiloop");
	public static final SoundEvent HOOO = register("hooo");
	public static final SoundEvent FEELIN_FEELING_GOOD = register("feelinfeelingood");
	public static final SoundEvent HOTLINE_PSYAMI = register("hotlinepsyami");
	public static final SoundEvent MJ_DIE = register("mjdie");
	public static final SoundEvent BOSS_OF_GYM_COME_ON_LETS_GO = register("bossofgymcomeonletsgo");
	public static final SoundEvent BEAT_IT_DOESNT_MATTER = register("beatit_doesntmatter");
	public static final SoundEvent BEAT_IT_NO_ONE_WANTS_TO_BE_DEFEATED = register("beatit_noonewantstobedefeated");
	public static final SoundEvent BEAT_IT_KICK_YOU_BEAT_YOU = register("beatit_kickyoubeatyou");
	public static final SoundEvent BET_YOU_REMEMBER = register("betyouremember");
	public static final SoundEvent _31_MIN = register("31min");
	public static final SoundEvent ASCEND_HEE_HEE = register("ascendheehee");
	public static final SoundEvent MISTRESS_DIE = register("mistressdie");
	public static final SoundEvent SUCTION = register("suction");
	public static final SoundEvent CITY_GUARD_MINI = register("cityguardmini");
	public static final SoundEvent CITY_GUARD_INTRO_LOOP = register("cityguardintroloop");
	public static final SoundEvent CITY_GUARD_MIDDLE = register("cittyguardmiddle");
	public static final SoundEvent CITY_GUARD_ATTACK_LOOP_TWO = register("cityguard_attack_loop_two");
	public static final SoundEvent THRILLER = register("thriller");
	public static final SoundEvent NEVER_FORGET = register("neverforget");
	public static final SoundEvent BEAT_IT_LOOP = register("beatitloop");
	public static final SoundEvent DEAREST_HELENA = register("dearesthelena");
	public static final SoundEvent MAN_IN_THE_MIRROR_START = register("maninthemirrorstart");
	public static final SoundEvent MAN_IN_THE_MIRROR_LOOP = register("maninthemirrorloop");
	public static final SoundEvent REMEMBER_THE_TIME_LOOP = register("rememberthetimeloop");
	public static final SoundEvent REMEMBER_THE_TIME_START = register("rememberthetimestart");

	private static SoundEvent register(String name) {
		return SoundEvent.of(Identifier.of("custom/" + name));
	}
}
