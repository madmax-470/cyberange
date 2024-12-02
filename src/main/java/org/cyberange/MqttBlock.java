package org.cyberange;

import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;

import net.minecraft.core.BlockPos;


public class MqttBlock extends Block {
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    private final MqttHelper mqttHelper;

    public MqttBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(3.0f, 6.0f)
                .requiresCorrectToolForDrops());
        this.registerDefaultState(this.getStateDefinition().any().setValue(POWERED, false));
        mqttHelper = new MqttHelper((topic, message) -> {
            String payload = new String(message.getPayload());
            if ("on".equalsIgnoreCase(payload)) {
                handlePowerStateChange(true);
            }
        });


        mqttHelper.subscribe("cy/irl-censor");
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }


    private void handlePowerStateChange(boolean powered) {
        this.defaultBlockState().setValue(POWERED, powered);
//        //if (!world.isRemote) {
//            BlockPos pos = this.getStateForPlacement() ;
//            BlockState currentState = world.getBlockState(pos);
//
//            world.setBlockState(pos, currentState.with(POWERED, powered), 3);
//        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide) {
            boolean hasPower = level.hasNeighborSignal(pos); // Checks if the block is receiving a redstone signal
            if (state.getValue(POWERED) != hasPower) {
                level.setBlock(pos, state.setValue(POWERED, hasPower), 3);
                mqttHelper.publish("cy/sensor",""+ pos + (hasPower)?"on":"off");// Updates the block state
            }

        }


    }
    @Override
    public boolean isSignalSource(BlockState state) {
        return state.getValue(POWERED);
    }




    @Override
    protected int getSignal(BlockState state, BlockGetter p_60484_, BlockPos p_60485_, Direction p_60486_) {
        return state.getValue(POWERED) ? 15 : 0; // Emits full power when powered
    }
}