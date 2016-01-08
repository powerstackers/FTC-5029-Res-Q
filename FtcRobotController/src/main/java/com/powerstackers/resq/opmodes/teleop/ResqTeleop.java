/*
 * Copyright (C) 2015 Powerstackers
 *
 * Teleop code for Res-Q.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.powerstackers.resq.opmodes.teleop;

import com.powerstackers.resq.common.DoorSetting;
import com.powerstackers.resq.common.MotorSetting;
import com.powerstackers.resq.common.Robot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.Range;

import org.swerverobotics.library.interfaces.TeleOp;

/**
 * This is the opmode for use on our competition robot during teleop.
 * @author Jonathan Thomas
 */
@TeleOp(name = "Res-Q Tele-op", group = "Powerstackers")
public class ResqTeleop extends OpMode {

    private static final float MINIMUM_JOYSTICK_THRESHOLD = 0.15F;

    Robot robot;

    /**
     * Initialize the robot.
     */
    @Override
    public void init() {
        robot = new Robot(this);
    }

    /**
     * Called continuously while the robot runs.
     */
    @Override
    public void loop() {

        // We like to use tank drive.
        float stickValueP1Left = -gamepad1.left_stick_y;
        float stickValueP1Right = -gamepad1.right_stick_y;
        MotorSetting settingTapeMeasureServo;
        MotorSetting settingLiftMotor;
        MotorSetting settingBrushMotor;

        // Read the joystick and determine what motor setting to use.
        stickValueP1Left = (float) scaleInput(Range.clip(stickValueP1Left, -1, 1));
        stickValueP1Right = (float) scaleInput(Range.clip(stickValueP1Right, -1, 1));

        // Set the lift motor value.
        if (gamepad1.a) {
            settingLiftMotor = MotorSetting.REVERSE;
        } else if (gamepad1.y) {
            settingLiftMotor = MotorSetting.FORWARD;
        } else {
            settingLiftMotor = MotorSetting.STOP;
        }

        // Set the brush motor value.
        if (gamepad1.x) {
            settingBrushMotor = MotorSetting.FORWARD;
        } else if (gamepad1.b) {
            settingBrushMotor = MotorSetting.REVERSE;
        } else {
            settingBrushMotor = MotorSetting.STOP;
        }

        // Set the tape measure value.
        if (gamepad2.a) {
            settingTapeMeasureServo = MotorSetting.REVERSE;
        } else if (gamepad2.y) {
            settingTapeMeasureServo = MotorSetting.FORWARD;
        } else {
            settingTapeMeasureServo = MotorSetting.STOP;
        }

        // Set the hopper doors.
        // Hopper left
        if (gamepad2.left_bumper) {
            robot.setHopperLeft(DoorSetting.OPEN);
        } else {
            robot.setHopperLeft(DoorSetting.CLOSE);
        }
        // Hopper right
        if (gamepad2.right_bumper) {
            robot.setHopperRight(DoorSetting.OPEN);
        } else {
            robot.setHopperRight(DoorSetting.CLOSE);
        }

        // Set the climber flipper value.
        if (gamepad2.right_trigger) {
            robot.setClimberFlipper(DoorSetting.OPEN);
        } else {
            robot.setClimberFlipper(DoorSetting.CLOSE);
        }

        // Last of all, update the motor values.
        if (abs(stickValueP1Left) > MINIMUM_JOYSTICK_THRESHOLD) {
            robot.setPowerLeft(stickValueP1Left);
        }
        if (abs(stickValueP1Right) > MINIMUM_JOYSTICK_THRESHOLD) {
            robot.setPowerRight(stickValueP1Right);
        }
        robot.setTapeMeasure(settingTapeMeasureServo);
        robot.setLift(settingLiftMotor);
        robot.setBrush(settingBrushMotor);
    }

    /**
     * Return the absolute value of a float.
     * @param in Floating point number.
     * @return Positive float.
     */
    private float abs(float in) {
        if (in > 0) {
            return in;
        } else {
            return -in;
        }
    }

    /**
     * This method scales the joystick input so for low joystick values, the
     * scaled value is less than linear.  This is to make it easier to drive
     * the robot more precisely at slower speeds.
     */
    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);

        // index should be positive.
        if (index < 0) {
            index = -index;
        }

        // index cannot exceed size of array minus 1.
        if (index > 16) {
            index = 16;
        }

        // get value from the array.
        double dScale;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        // return scaled value.
        return dScale;
    }

}
