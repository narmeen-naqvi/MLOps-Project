from airflow import DAG
from airflow.operators.dummy_operator import DummyOperator
from airflow.operators.bash_operator import BashOperator
from datetime import datetime


dag = DAG(
    'fyp_workflow',
    description='FYP Workflow',
    schedule_interval='@monthly',
    start_date=datetime(2023, 6, 15),
)

# Define tasks
start_task = DummyOperator(task_id='start_task', dag=dag)

task1 = BashOperator(
    task_id='task1',
    bash_command='echo "Executing task 1"',
    dag=dag,
)

task2 = BashOperator(
    task_id='task2',
    bash_command='echo "Executing task 2"',
    dag=dag,
)

end_task = DummyOperator(task_id='end_task', dag=dag)

start_task >> task1
start_task >> task2
task1 >> end_task
task2 >> end_task
