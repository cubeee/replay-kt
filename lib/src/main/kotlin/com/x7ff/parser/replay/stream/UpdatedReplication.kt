package com.x7ff.parser.replay.stream

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.exception.UnknownPropertyException
import com.x7ff.parser.replay.ClassNetCacheProperty
import com.x7ff.parser.replay.ObjectReference
import com.x7ff.parser.replay.Vector3d.Companion.readVector
import com.x7ff.parser.replay.Versions
import com.x7ff.parser.replay.attribute.AppliedDamageAttribute.Companion.readAppliedDamage
import com.x7ff.parser.replay.attribute.ClientLoadoutOnlineAttribute.Companion.readClientLoadoutOnline
import com.x7ff.parser.replay.attribute.ClubColorsAttribute.Companion.readClubColors
import com.x7ff.parser.replay.attribute.DamageStateAttribute.Companion.readDamageState
import com.x7ff.parser.replay.attribute.DemolishAttribute.Companion.readDemolish
import com.x7ff.parser.replay.attribute.ExplosionAttribute.Companion.readExplosion
import com.x7ff.parser.replay.attribute.ExtendedExplosionAttribute.Companion.readExtendedExplosion
import com.x7ff.parser.replay.attribute.GameModeAttribute.Companion.readGameMode
import com.x7ff.parser.replay.attribute.LoadoutAttribute.Companion.readLoadout
import com.x7ff.parser.replay.attribute.LoadoutsAttribute.Companion.readLoadouts
import com.x7ff.parser.replay.attribute.LoadoutsOnlineAttribute.Companion.readLoadoutsOnline
import com.x7ff.parser.replay.attribute.MusicStingerAttribute.Companion.readMusicStinger
import com.x7ff.parser.replay.attribute.PartyLeaderAttribute.Companion.readPartyLeader
import com.x7ff.parser.replay.attribute.PickupAttribute.Companion.readPickupData
import com.x7ff.parser.replay.attribute.PrivateMatchSettingsAttribute.Companion.readPrivateMatchSettings
import com.x7ff.parser.replay.attribute.RepStatTitlesAttribute.Companion.readRepStatTitles
import com.x7ff.parser.replay.attribute.ReservationAttribute.Companion.readReservation
import com.x7ff.parser.replay.attribute.RigidBodyStateAttribute.Companion.readRigidBodyState
import com.x7ff.parser.replay.attribute.StatEventAttribute.Companion.readStatEvent
import com.x7ff.parser.replay.attribute.TeamPaint.Companion.readTeamPaint
import com.x7ff.parser.replay.attribute.TitleAttribute.Companion.readTitle
import com.x7ff.parser.replay.attribute.UniqueIdAttribute.Companion.readUniqueId
import com.x7ff.parser.replay.attribute.WeldedInfoAttribute.Companion.readWeldedInfo
import com.x7ff.parser.replay.stream.ActiveActor.Companion.readActiveActor
import com.x7ff.parser.replay.stream.CameraSettings.Companion.readCameraSettings
import com.x7ff.parser.replay.stream.ObjectTarget.Companion.readObjectTarget

data class UpdatedReplication(
    val propertyId: Long,
    val property: ClassNetCacheProperty?,
    val propertyName: String,
    val data: Any
): ReplicationValue {
    companion object {
        fun BitBuffer.readUpdatedReplication(
            versions: Versions,
            previousReplication: SpawnedReplication,
            objectReferences: List<ObjectReference>
        ): UpdatedReplication {
            val classMap = previousReplication.classMap
            val limit = classMap.maxPropertyId
            val propertyId = getUIntMax(limit + 1)
            val property = classMap.getProperty(propertyId.toInt())
            val propertyName = objectReferences[property!!.index].name

            val data: Any = when(propertyName) {
                "TAGame.CrowdManager_TA:GameEvent" -> readActiveActor()
                "TAGame.CrowdActor_TA:GameEvent" -> readActiveActor()
                "TAGame.PRI_TA:PersistentCamera" -> readActiveActor()
                "TAGame.Team_TA:GameEvent" -> readActiveActor()
                "TAGame.Ball_TA:GameEvent" -> readActiveActor()
                "Engine.PlayerReplicationInfo:Team" -> readActiveActor()
                "Engine.Pawn:PlayerReplicationInfo" -> readActiveActor()
                "TAGame.PRI_TA:ReplicatedGameEvent" -> readActiveActor()
                "TAGame.CarComponent_TA:Vehicle" -> readActiveActor()
                "TAGame.Car_TA:AttachedPickup" -> readActiveActor()
                "TAGame.SpecialPickup_Targeted_TA:Targeted" -> readActiveActor()
                "TAGame.CameraSettingsActor_TA:PRI" -> readActiveActor()
                "TAGame.Team_TA:LogoData" -> readActiveActor()
                "TAGame.GameEvent_Soccar_TA:MVP" -> readActiveActor()
                "TAGame.GameEvent_Soccar_TA:MatchWinner" -> readActiveActor()
                "TAGame.GameEvent_Soccar_TA:GameWinner" -> readActiveActor()

                "TAGame.CrowdManager_TA:ReplicatedGlobalOneShotSound" -> readObjectTarget()
                "TAGame.CrowdActor_TA:ReplicatedOneShotSound" -> readObjectTarget()
                "TAGame.GameEvent_TA:MatchTypeClass" -> readObjectTarget()
                "Engine.GameReplicationInfo:GameClass" -> readObjectTarget()
                "TAGame.GameEvent_Soccar_TA:SubRulesArchetype" -> readObjectTarget()

                "TAGame.CameraSettingsActor_TA:ProfileSettings" -> readCameraSettings(versions)
                "TAGame.PRI_TA:CameraSettings" -> readCameraSettings(versions)
                "Engine.PlayerReplicationInfo:UniqueId" -> readUniqueId(versions)
                "TAGame.PRI_TA:ClientLoadoutsOnline" -> readLoadoutsOnline(versions, objectReferences)
                "TAGame.PRI_TA:ClientLoadouts" -> readLoadouts()
                "TAGame.PRI_TA:ClientLoadoutOnline" -> readClientLoadoutOnline(versions, objectReferences)
                "TAGame.RBActor_TA:ReplicatedRBState" -> readRigidBodyState(versions)
                "TAGame.Car_TA:TeamPaint" -> readTeamPaint()
                "ProjectX.GRI_X:Reservations" -> readReservation(versions)
                "TAGame.VehiclePickup_TA:ReplicatedPickupData" -> readPickupData()
                "Engine.Actor:Location" -> readVector(versions)
                "TAGame.CarComponent_Dodge_TA:DodgeTorque" -> readVector(versions)
                "TAGame.GameEvent_Soccar_TA:ReplicatedStatEvent" -> readStatEvent()
                "TAGame.Car_TA:ReplicatedDemolish" -> readDemolish(versions)
                "TAGame.Ball_TA:ReplicatedExplosionData" -> readExplosion(versions)
                "TAGame.Ball_TA:ReplicatedExplosionDataExtended" -> readExtendedExplosion(versions)
                "TAGame.PRI_TA:PartyLeader" -> readPartyLeader(versions)
                "TAGame.PRI_TA:ClientLoadout" -> readLoadout()
                "TAGame.PRI_TA:PrimaryTitle" -> readTitle()
                "TAGame.PRI_TA:SecondaryTitle" -> readTitle()
                "TAGame.GameEvent_Soccar_TA:ReplicatedMusicStinger" -> readMusicStinger()
                "TAGame.RBActor_TA:WeldedInfo" -> readWeldedInfo(versions)
                "TAGame.Team_TA:ClubColors" -> readClubColors()
                "TAGame.Car_TA:ClubColors" -> readClubColors()
                "TAGame.GameEvent_SoccarPrivate_TA:MatchSettings" -> readPrivateMatchSettings()
                "TAGame.GameEvent_TA:GameMode" -> readGameMode(versions)
                "TAGame.BreakOutActor_Platform_TA:DamageState" -> readDamageState(versions)
                "TAGame.Ball_Breakout_TA:AppliedDamage" -> readAppliedDamage(versions)
                "TAGame.PRI_TA:RepStatTitles" -> readRepStatTitles()

                "Engine.GameReplicationInfo:ServerName" -> getFixedLengthString()
                "Engine.PlayerReplicationInfo:PlayerName" -> getFixedLengthString()
                "TAGame.Team_TA:CustomTeamName" -> getFixedLengthString()
                "Engine.PlayerReplicationInfo:RemoteUserData" -> getFixedLengthString()
                "TAGame.GRI_TA:NewDedicatedServerIP" -> getFixedLengthString()
                "ProjectX.GRI_X:MatchGUID" -> getFixedLengthString()

                "TAGame.GameEvent_Soccar_TA:SecondsRemaining" -> getUInt()
                "TAGame.GameEvent_TA:ReplicatedGameStateTimeRemaining" -> getUInt()
                "TAGame.CrowdActor_TA:ReplicatedCountDownNumber" -> getUInt()
                "TAGame.GameEvent_Team_TA:MaxTeamSize" -> getUInt()
                "Engine.PlayerReplicationInfo:PlayerID" -> getUInt()
                "TAGame.PRI_TA:TotalXP" -> getUInt()
                "TAGame.PRI_TA:MatchScore" -> getUInt()
                "TAGame.GameEvent_Soccar_TA:RoundNum" -> getUInt()
                "TAGame.GameEvent_TA:BotSkill" -> getUInt()
                "TAGame.PRI_TA:MatchShots" -> getUInt()
                "TAGame.PRI_TA:MatchSaves" -> getUInt()
                "ProjectX.GRI_X:ReplicatedGamePlaylist" -> getUInt()
                "Engine.TeamInfo:Score" -> getUInt()
                "Engine.PlayerReplicationInfo:Score" -> getUInt()
                "TAGame.PRI_TA:MatchGoals" -> getUInt()
                "TAGame.PRI_TA:MatchAssists" -> getUInt()
                "TAGame.PRI_TA:Title" -> getUInt()
                "TAGame.GameEvent_TA:ReplicatedStateName" -> getUInt()
                "TAGame.Team_Soccar_TA:GameScore" -> getUInt()
                "TAGame.GameEvent_Soccar_TA:GameTime" -> getUInt()
                "TAGame.CarComponent_Boost_TA:UnlimitedBoostRefCount" -> getUInt()
                "TAGame.CrowdActor_TA:ReplicatedRoundCountDownNumber" -> getUInt()
                "TAGame.PRI_TA:MaxTimeTillItem" -> getUInt()
                "TAGame.Ball_Breakout_TA:DamageIndex" -> getUInt()
                "TAGame.PRI_TA:MatchBreakoutDamage" -> getUInt()
                "TAGame.PRI_TA:BotProductName" -> getUInt()
                "TAGame.GameEvent_TA:ReplicatedRoundCountDownNumber" -> getUInt()
                "TAGame.GameEvent_Soccar_TA:SeriesLength" -> getUInt()
                "TAGame.PRI_TA:SpectatorShortcut" -> getUInt()

                "ProjectX.GRI_X:ReplicatedGameMutatorIndex" -> getInt()
                "TAGame.PRI_TA:TimeTillItem" -> getInt()

                "Engine.PlayerReplicationInfo:Ping" -> getUByte()
                "TAGame.Vehicle_TA:ReplicatedSteer" -> getUByte()
                "TAGame.Vehicle_TA:ReplicatedThrottle" -> getUByte() // 0: full reverse, 128: No throttle.  255 full throttle/boosting
                "TAGame.PRI_TA:CameraYaw" -> getUByte()
                "TAGame.PRI_TA:CameraPitch" -> getUByte()
                "TAGame.Ball_TA:HitTeamNum" -> getUByte()
                "TAGame.GameEvent_Soccar_TA:ReplicatedScoredOnTeam" -> getUByte()
                "TAGame.CarComponent_Boost_TA:ReplicatedBoostAmount" -> getUByte()
                "TAGame.CameraSettingsActor_TA:CameraPitch" -> getUByte()
                "TAGame.CameraSettingsActor_TA:CameraYaw" -> getUByte()
                "TAGame.PRI_TA:PawnType" -> getUByte()
                "TAGame.Ball_Breakout_TA:LastTeamTouch" -> getUByte()
                "TAGame.PRI_TA:ReplicatedWorstNetQualityBeyondLatency" -> getUByte()
                "TAGame.GameEvent_Soccar_TA:ReplicatedServerPerformanceState" -> getUByte()
                "TAGame.CarComponent_TA:ReplicatedActive" -> getUByte()
                "TAGame.GameEvent_TA:ReplicatedStateIndex" -> getUByte()

                "Engine.Actor:bCollideWorld" -> getBoolean()
                "Engine.PlayerReplicationInfo:bReadyToPlay" -> getBoolean()
                "TAGame.Vehicle_TA:bReplicatedHandbrake" -> getBoolean()
                "TAGame.Vehicle_TA:bDriving" -> getBoolean()
                "Engine.Actor:bNetOwner" -> getBoolean()
                "Engine.Actor:bBlockActors" -> getBoolean()
                "TAGame.GameEvent_TA:bHasLeaveMatchPenalty" -> getBoolean()
                "TAGame.PRI_TA:bUsingBehindView" -> getBoolean()
                "TAGame.PRI_TA:bUsingSecondaryCamera" -> getBoolean() // Ball cam on when true
                "TAGame.GameEvent_TA:ActivatorCar" -> getBoolean()
                "TAGame.GameEvent_Soccar_TA:bOverTime" -> getBoolean()
                "ProjectX.GRI_X:bGameStarted" -> getBoolean()
                "Engine.Actor:bCollideActors" -> getBoolean()
                "TAGame.PRI_TA:bReady" -> getBoolean()
                "TAGame.RBActor_TA:bFrozen" -> getBoolean()
                "Engine.Actor:bHidden" -> getBoolean()
                "TAGame.CarComponent_FlipCar_TA:bFlipRight" -> getBoolean()
                "Engine.PlayerReplicationInfo:bBot" -> getBoolean()
                "Engine.PlayerReplicationInfo:bWaitingPlayer" -> getBoolean()
                "TAGame.RBActor_TA:bReplayActor" -> getBoolean()
                "TAGame.PRI_TA:bIsInSplitScreen" -> getBoolean()
                "Engine.GameReplicationInfo:bMatchIsOver" -> getBoolean()
                "TAGame.CarComponent_Boost_TA:bUnlimitedBoost" -> getBoolean()
                "Engine.PlayerReplicationInfo:bIsSpectator" -> getBoolean()
                "TAGame.GameEvent_Soccar_TA:bBallHasBeenHit" -> getBoolean()
                "TAGame.CameraSettingsActor_TA:bUsingSecondaryCamera" -> getBoolean()
                "TAGame.CameraSettingsActor_TA:bUsingBehindView" -> getBoolean()
                "TAGame.PRI_TA:bOnlineLoadoutSet" -> getBoolean()
                "TAGame.PRI_TA:bMatchMVP" -> getBoolean()
                "TAGame.PRI_TA:bOnlineLoadoutsSet" -> getBoolean()
                "TAGame.RBActor_TA:bIgnoreSyncing" -> getBoolean()
                "TAGame.SpecialPickup_BallVelcro_TA:bHit" -> getBoolean()
                "TAGame.GameEvent_TA:bCanVoteToForfeit" -> getBoolean()
                "TAGame.SpecialPickup_BallVelcro_TA:bBroken" -> getBoolean()
                "TAGame.GameEvent_Team_TA:bForfeit" -> getBoolean()
                "TAGame.PRI_TA:bUsingItems" -> getBoolean()
                "TAGame.VehiclePickup_TA:bNoPickup" -> getBoolean()
                "TAGame.CarComponent_Boost_TA:bNoBoost" -> getBoolean()
                "TAGame.PRI_TA:PlayerHistoryValid" -> getBoolean()
                "TAGame.GameEvent_Soccar_TA:bMatchEnded" -> getBoolean()
                "TAGame.GameEvent_Soccar_TA:bUnlimitedTime" -> getBoolean()

                "TAGame.CarComponent_FlipCar_TA:FlipCarTime" -> getFloat()
                "TAGame.Ball_TA:ReplicatedBallScale" -> getFloat()
                "TAGame.CarComponent_Boost_TA:RechargeDelay" -> getFloat()
                "TAGame.CarComponent_Boost_TA:RechargeRate" -> getFloat()
                "TAGame.Ball_TA:ReplicatedAddedCarBounceScale" -> getFloat()
                "TAGame.Ball_TA:ReplicatedBallMaxLinearSpeedScale" -> getFloat()
                "TAGame.Ball_TA:ReplicatedWorldBounceScale" -> getFloat()
                "TAGame.CarComponent_Boost_TA:BoostModifier" -> getFloat()
                "Engine.Actor:DrawScale" -> getFloat()
                "TAGame.CrowdActor_TA:ModifiedNoise" -> getFloat()
                "TAGame.CarComponent_TA:ReplicatedActivityTime" -> getFloat()
                "TAGame.SpecialPickup_BallFreeze_TA:RepOrigSpeed" -> getFloat()
                "TAGame.SpecialPickup_BallVelcro_TA:AttachTime" -> getFloat()
                "TAGame.SpecialPickup_BallVelcro_TA:BreakTime" -> getFloat()
                "TAGame.Car_TA:AddedCarForceMultiplier" -> getFloat()
                "TAGame.Car_TA:AddedBallForceMultiplier" -> getFloat()
                "TAGame.PRI_TA:SteeringSensitivity" -> getFloat()
                "TAGame.Car_TA:ReplicatedCarScale" -> getFloat()

                "ProjectX.GRI_X:GameServerID" -> getLong()

                "TAGame.PRI_TA:ClubID" -> getInt64()
                "TAGame.Team_TA:ClubID" -> getInt64()

                "TAGame.PRI_TA:PlayerHistoryKey" -> getBits(14).toInt()
                "TAGame.PRI_TA:SkillTier" -> getBits(9)

                else -> {
                    println("Next 2048 bits: ")
                    for (i in 0..2048) {
                        print(getBits(1))
                    }
                    println()
                    throw UnknownPropertyException("Unknown property name '$propertyName'")
                }
            }
            return UpdatedReplication(propertyId, property, propertyName, data)
        }

    }
}