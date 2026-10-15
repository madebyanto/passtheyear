package com.example.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class SchoolYearMetrics(
    val daysRemaining: Long,
    val progressPercent: Float, // 0 to 100
    val actualSchoolDaysRemaining: Long,
    val totalSchoolDays: Long,
    val saturdayFree: Boolean = true,
    val isFinished: Boolean,
    val isNotStartedYet: Boolean
)

enum class MonthProgressStatus {
    COMPLETED,
    IN_PROGRESS,
    UPCOMING
}

data class MonthProgressInfo(
    val monthIndex: Int,
    val progressPercent: Int, // 0 to 100
    val status: MonthProgressStatus,
    val daysPassed: Int,
    val totalDays: Int
)

object SchoolCalendarUtils {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun parseDate(dateStr: String): LocalDate? {
        return try {
            LocalDate.parse(dateStr, formatter)
        } catch (e: Exception) {
            null
        }
    }

    fun formatDate(date: LocalDate): String {
        return date.format(formatter)
    }

    private fun isSchoolDay(date: LocalDate, saturdayFree: Boolean): Boolean {
        return if (saturdayFree) {
            date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY
        } else {
            date.dayOfWeek != DayOfWeek.SUNDAY
        }
    }

    fun calculateMetrics(
        startDateStr: String,
        endDateStr: String,
        saturdayFree: Boolean = true,
        today: LocalDate = LocalDate.now()
    ): SchoolYearMetrics {
        val start = parseDate(startDateStr) ?: today.minusMonths(2)
        val end = parseDate(endDateStr) ?: today.plusMonths(7)

        val totalCalendarDays = ChronoUnit.DAYS.between(start, end).coerceAtLeast(1)
        val daysFromStart = ChronoUnit.DAYS.between(start, today)
        val daysRemaining = ChronoUnit.DAYS.between(today, end).coerceAtLeast(0)

        val progressPercent = when {
            today.isBefore(start) -> 0f
            today.isAfter(end) -> 100f
            else -> {
                val ratio = daysFromStart.toFloat() / totalCalendarDays.toFloat()
                (ratio * 100f).coerceIn(0f, 100f)
            }
        }

        // Calculate actual school days from max(today, start) to end
        val fromDate = if (today.isBefore(start)) start else today
        var schoolDaysRemaining = 0L

        if (!fromDate.isAfter(end)) {
            var curr = fromDate
            while (!curr.isAfter(end)) {
                if (isSchoolDay(curr, saturdayFree)) {
                    schoolDaysRemaining++
                }
                curr = curr.plusDays(1)
            }
        }

        // Total school days across the entire academic year
        var totalSchoolDays = 0L
        var cur = start
        while (!cur.isAfter(end)) {
            if (isSchoolDay(cur, saturdayFree)) {
                totalSchoolDays++
            }
            cur = cur.plusDays(1)
        }

        return SchoolYearMetrics(
            daysRemaining = daysRemaining,
            progressPercent = progressPercent,
            actualSchoolDaysRemaining = schoolDaysRemaining,
            totalSchoolDays = totalSchoolDays,
            saturdayFree = saturdayFree,
            isFinished = today.isAfter(end),
            isNotStartedYet = today.isBefore(start)
        )
    }

    fun calculateMonthProgress(
        monthIndex: Int,
        startDateStr: String? = null,
        endDateStr: String? = null,
        today: LocalDate = LocalDate.now()
    ): MonthProgressInfo {
        val start = startDateStr?.takeIf { it.isNotBlank() }?.let { parseDate(it) } ?: LocalDate.of(
            if (today.monthValue >= 8) today.year else today.year - 1, 9, 1
        )
        val end = endDateStr?.takeIf { it.isNotBlank() }?.let { parseDate(it) } ?: LocalDate.of(
            if (today.monthValue >= 8) today.year + 1 else today.year, 6, 15
        )

        val targetYear = if (start.year == end.year) {
            start.year
        } else {
            if (monthIndex >= start.monthValue) start.year else end.year
        }

        val monthFirstDay = LocalDate.of(targetYear, monthIndex, 1)
        val monthLastDay = LocalDate.of(targetYear, monthIndex, monthFirstDay.lengthOfMonth())

        val effectiveStart = if (start.isAfter(monthFirstDay)) start else monthFirstDay
        val effectiveEnd = if (end.isBefore(monthLastDay)) end else monthLastDay

        if (effectiveStart.isAfter(effectiveEnd)) {
            return if (monthLastDay.isBefore(start)) {
                MonthProgressInfo(monthIndex, 0, MonthProgressStatus.UPCOMING, 0, monthFirstDay.lengthOfMonth())
            } else {
                MonthProgressInfo(monthIndex, 100, MonthProgressStatus.COMPLETED, monthFirstDay.lengthOfMonth(), monthFirstDay.lengthOfMonth())
            }
        }

        val totalDays = (ChronoUnit.DAYS.between(effectiveStart, effectiveEnd) + 1).toInt().coerceAtLeast(1)

        return when {
            today.isAfter(effectiveEnd) -> {
                MonthProgressInfo(
                    monthIndex = monthIndex,
                    progressPercent = 100,
                    status = MonthProgressStatus.COMPLETED,
                    daysPassed = totalDays,
                    totalDays = totalDays
                )
            }
            today.isBefore(effectiveStart) -> {
                MonthProgressInfo(
                    monthIndex = monthIndex,
                    progressPercent = 0,
                    status = MonthProgressStatus.UPCOMING,
                    daysPassed = 0,
                    totalDays = totalDays
                )
            }
            else -> {
                val daysPassed = (ChronoUnit.DAYS.between(effectiveStart, today) + 1).toInt().coerceAtLeast(1)
                val pct = ((daysPassed.toFloat() / totalDays.toFloat()) * 100).toInt().coerceIn(1, 99)
                MonthProgressInfo(
                    monthIndex = monthIndex,
                    progressPercent = pct,
                    status = MonthProgressStatus.IN_PROGRESS,
                    daysPassed = daysPassed,
                    totalDays = totalDays
                )
            }
        }
    }
}
