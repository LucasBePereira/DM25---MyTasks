package com.lucas.mytasks.listener

import com.lucas.mytasks.entity.Task

interface ClickListener {

    fun onClick(task: Task)

    fun onComplete(id: Long)

}