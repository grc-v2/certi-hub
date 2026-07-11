package com.certimaster.productivity.application.command;

import com.certimaster.productivity.adapter.persistence.HabitCheckinRepository;
import com.certimaster.productivity.adapter.persistence.HabitRepository;
import com.certimaster.productivity.adapter.web.mapper.HabitMapper;
import com.certimaster.productivity.domain.entity.Habit;
import com.certimaster.productivity.domain.entity.HabitCheckin;
import com.certimaster.productivity.domain.entity.HabitFrequency;
import com.certimaster.productivity.domain.service.HabitStreakCalculator;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitCommandServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitCheckinRepository checkinRepository;

    @Mock
    private HabitMapper habitMapper;

    @Mock
    private HabitStreakCalculator streakCalculator;

    @InjectMocks
    private HabitCommandService service;

    private void stubEmptyCheckinHistory() {
        when(checkinRepository.findByHabitIdAndCheckinDateBetween(any(), any(), any()))
                .thenReturn(List.of());
    }

    @Test
    void createBuildsActiveHabitWithSortedWeekdayCsv() {
        stubEmptyCheckinHistory();
        when(habitRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.create(new CreateHabitCommand(7L, "Đọc sách", "#22c55e", HabitFrequency.WEEKLY,
                Set.of(DayOfWeek.WEDNESDAY, DayOfWeek.MONDAY), LocalTime.of(21, 0)));

        ArgumentCaptor<Habit> captor = ArgumentCaptor.forClass(Habit.class);
        verify(habitRepository).save(captor.capture());
        Habit saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(7L);
        assertThat(saved.getName()).isEqualTo("Đọc sách");
        assertThat(saved.getFrequency()).isEqualTo(HabitFrequency.WEEKLY);
        assertThat(saved.getWeekdays()).isEqualTo("MONDAY,WEDNESDAY");
        assertThat(saved.getReminderTime()).isEqualTo(LocalTime.of(21, 0));
        assertThat(saved.isActive()).isTrue();
    }

    @Test
    void updateClearReminderRemovesReminderTimeAndLeavesOtherFields() {
        Habit habit = Habit.builder().userId(1L).name("Gym")
                .frequency(HabitFrequency.DAILY).reminderTime(LocalTime.of(6, 30)).build();
        when(habitRepository.findByIdAndUserId(3L, 1L)).thenReturn(Optional.of(habit));
        stubEmptyCheckinHistory();

        service.update(new UpdateHabitCommand(3L, 1L, null, null, null, null,
                LocalTime.of(7, 0), true, null, null));

        assertThat(habit.getReminderTime()).isNull();
        assertThat(habit.getName()).isEqualTo("Gym");
        assertThat(habit.getFrequency()).isEqualTo(HabitFrequency.DAILY);
    }

    @Test
    void updateUnknownOrForeignHabitFails() {
        when(habitRepository.findByIdAndUserId(3L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(new UpdateHabitCommand(3L, 2L, "x", null, null,
                null, null, false, null, null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void setCheckinDoneCreatesCheckinWhenNotYetDone() {
        Habit habit = Habit.builder().userId(1L).name("Gym").build();
        habit.setId(3L);
        LocalDate date = LocalDate.of(2026, 7, 10);
        when(habitRepository.findByIdAndUserId(3L, 1L)).thenReturn(Optional.of(habit));
        when(checkinRepository.existsByHabitIdAndCheckinDate(3L, date)).thenReturn(false);
        stubEmptyCheckinHistory();

        service.setCheckin(new SetHabitCheckinCommand(3L, 1L, date, true));

        ArgumentCaptor<HabitCheckin> captor = ArgumentCaptor.forClass(HabitCheckin.class);
        verify(checkinRepository).save(captor.capture());
        assertThat(captor.getValue().getHabitId()).isEqualTo(3L);
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
        assertThat(captor.getValue().getCheckinDate()).isEqualTo(date);
    }

    @Test
    void setCheckinDoneIsIdempotentWhenAlreadyCheckedIn() {
        Habit habit = Habit.builder().userId(1L).name("Gym").build();
        habit.setId(3L);
        LocalDate date = LocalDate.of(2026, 7, 10);
        when(habitRepository.findByIdAndUserId(3L, 1L)).thenReturn(Optional.of(habit));
        when(checkinRepository.existsByHabitIdAndCheckinDate(3L, date)).thenReturn(true);
        stubEmptyCheckinHistory();

        service.setCheckin(new SetHabitCheckinCommand(3L, 1L, date, true));

        verify(checkinRepository, never()).save(any());
    }

    @Test
    void setCheckinUndoneDeletesExistingCheckin() {
        Habit habit = Habit.builder().userId(1L).name("Gym").build();
        habit.setId(3L);
        LocalDate date = LocalDate.of(2026, 7, 10);
        HabitCheckin existing = new HabitCheckin(3L, 1L, date);
        when(habitRepository.findByIdAndUserId(3L, 1L)).thenReturn(Optional.of(habit));
        when(checkinRepository.findByHabitIdAndCheckinDate(3L, date)).thenReturn(Optional.of(existing));
        stubEmptyCheckinHistory();

        service.setCheckin(new SetHabitCheckinCommand(3L, 1L, date, false));

        verify(checkinRepository).delete(existing);
        verify(checkinRepository, never()).save(any());
    }

    @Test
    void setCheckinOnUnknownHabitFails() {
        when(habitRepository.findByIdAndUserId(9L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setCheckin(new SetHabitCheckinCommand(9L, 1L, null, true)))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(checkinRepository, never()).save(any());
    }

    @Test
    void deleteRemovesCheckinsBeforeHabit() {
        Habit habit = Habit.builder().userId(1L).name("Gym").build();
        habit.setId(3L);
        when(habitRepository.findByIdAndUserId(3L, 1L)).thenReturn(Optional.of(habit));

        service.delete(new DeleteHabitCommand(3L, 1L));

        InOrder inOrder = Mockito.inOrder(checkinRepository, habitRepository);
        inOrder.verify(checkinRepository).deleteByHabitId(3L);
        inOrder.verify(habitRepository).delete(habit);
    }
}
