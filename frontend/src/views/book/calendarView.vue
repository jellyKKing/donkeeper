<template>
  <div class="DivH">
    <div class="sec_cal">
      <!-- 캘린더 nav -->
      <div class="cal_nav">
        <a href="javascript:;" class="nav-btn go-prev" @click="goPrev">prev</a>
        <div class="year-month">{{ yearMonth }}</div>
        <a href="javascript:;" class="nav-btn go-next" @click="goNext">next</a>
      </div>

      <div style="display:flex; justify-content:center;">
        <div style="display:flex; justify-content:space-between; width: 70%;">
          <div>
            <div id="subTitle">
              수입
            </div>
            <div style="color:#4285F4;">
              {{ incom }}
            </div>
          </div>

          <div>
            <div id="subTitle">
              지출
            </div>
            <div style="color:#EA4335">
              {{ spend }}
            </div>
          </div>
        </div>
      </div>


      <!-- <div>수입 <p id="income">{{ incom }}</p> | 지출 {{ spend }} | 잔액 {{ incom - spend }}</div> -->
      <div class="cal_wrap">
        <!-- 요일 -->
        <div class="days">
          <div class="day">MON</div>
          <div class="day">TUE</div>
          <div class="day">WED</div>
          <div class="day">THU</div>
          <div class="day">FRI</div>
          <div class="day">SAT</div>
          <div class="day">SUN</div>
        </div>
        <!-- 날짜들 -->
        <div class="dates">
          <div v-for="day in days" :class="day.class" :key="day.index" style="height:65px;">
            {{ day.date }}
          </div>
        </div>
      </div>

      <div style="display:flex; justify-content: end; margin-top: 38px;">
        <p v-on:click="this.$router.push('/book/list')">
          <img src="@/assets/list.png" id="calBtn">
        </p>

        <!-- <p v-on:click="null">
          <img src="@/assets/copy.png" id="calBtn">
        </p> -->

        <div v-on:click="this.$router.push('/challenge')"
        style="display: flex; margin-top: 16px; font-weight: bold; justify-content: center; color: white; font-size: 13px; padding: 3px;  align-items: center; background-color:#34A853; width: 40px; height: 40px; border-radius: 25px;">
          챌린지
        </div>

        <p v-on:click="this.$router.push('/book/add')">
          <img src="@/assets/add.png" id="addBtn">
        </p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      today: new Date(),
      year:new Date().getFullYear(),
      month:new Date().getMonth(),
      days: [],
      // 임시데이터
      incom: 0,
      spend: 0,
      datas: [],
    };
  },
  computed: {
    yearMonth() {
      return `${this.year}.${this.month + 1}`;
    }
  },
  methods: {
    getData(){
      this.axios.get(process.env.VUE_APP_API_URL + `/account-book/total/amount/${this.year}/${this.month + 1}`)
      .then(res=>{
        console.log(res.data)
        this.incom = res.data.total.totalIncomeAmount.toLocaleString();
        this.spend = res.data.total.totalSpendingAmount.toLocaleString();
        this.datas = res.data.details

        return this.renderCalendar();
      })
      .then(()=>{
        this.addData()
      })
    },
    addData() {
      // const testdayday = this.$refs.incomRefs;
      const dayday = document.querySelectorAll('.current');
      dayday.forEach((day, idx) => {
        let p1 = document.createElement("p");
        p1.setAttribute('class', 'incom')
        p1.innerText = this.datas[idx].incomeAmount.toLocaleString();
        if(this.datas[idx].incomeAmount != 0) {          
          day.appendChild(p1);
        }
        let p2 = document.createElement("p");
        p2.setAttribute('class', 'spend')
        p2.innerText = this.datas[idx].spendingAmount.toLocaleString();
        if(this.datas[idx].spendingAmount != 0) {
          day.appendChild(p2);
        }
      })
    },
    renderCalendar() {
      let startDay = new Date(this.year, this.month, 0);
      let prevDate = startDay.getDate();
      let prevDay = startDay.getDay();
      console.log(startDay);
      let endDay = new Date(this.year, this.month + 1, 0);
      let nextDate = endDay.getDate();
      let nextDay = endDay.getDay();
      console.log(endDay);
      let days = [];

      for (let i = prevDate - prevDay + 1; i <= prevDate; i++) {
        days.push({ date: i, class: "day prev disable" });
      }
      for (let i = 1; i <= nextDate; i++) {
        days.push({ date: i, class: "day current" });
      }
      for (let i = 1; i <= (7 - nextDay == 7 ? 0 : 7 - nextDay); i++) {
        days.push({ date: i, class: "day next disable" });
      }

      if (this.today.getMonth() === this.month && this.today.getFullYear() === this.year) {
        let todayDate = this.today.getDate();
        days[prevDay + todayDate - 1].class += " today";
      }

      this.days = days;
    },
    goPrev() {
      const day = new Date(this.year, this.month - 1, 1);
      this.year = day.getFullYear()
      this.month = day.getMonth()
      this.getData()

    },
    goNext() {
      const day = new Date(this.year, this.month + 1, 1);
      this.year = day.getFullYear()
      this.month = day.getMonth()
      this.getData()

    }
  },
  mounted() {
    this.getData()
    // await this.renderCalendar();
    // await this.addData();
  }
};
</script>

<style>
.sec_cal {
  width: 360px;
  margin: 0 auto;
  font-family: "NotoSansR";
}

.sec_cal .cal_nav {
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: 700;
  font-size: 33px;
  line-height: 78px;
}

.sec_cal .cal_nav .year-month {
  width: 300px;
  text-align: center;
  line-height: 1;
}

.sec_cal .cal_nav .nav {
  display: flex;
  border: 1px solid #333333;
  border-radius: 5px;
}

.sec_cal .cal_nav .go-prev,
.sec_cal .cal_nav .go-next {
  display: block;
  width: 50px;
  height: 78px;
  font-size: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.sec_cal .cal_nav .go-prev::before,
.sec_cal .cal_nav .go-next::before {
  content: "";
  display: block;
  width: 10px;
  height: 10px;
  border: 3px solid #000;
  border-width: 3px 3px 0 0;
  transition: border 0.1s;
}

.sec_cal .cal_nav .go-prev:hover::before,
.sec_cal .cal_nav .go-next:hover::before {
  border-color: #4285F4;
}

.sec_cal .cal_nav .go-prev::before {
  transform: rotate(-135deg);
}

.sec_cal .cal_nav .go-next::before {
  transform: rotate(45deg);
}

.sec_cal .cal_wrap {
  padding-top: 40px;
  position: relative;
  margin: 0 auto;
}

.sec_cal .cal_wrap .days {
  display: flex;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ddd;
}

.sec_cal .cal_wrap::after {
  top: 368px;
}

.sec_cal .cal_wrap .day {
  /* display:flex; */
  align-items: center;
  justify-content: center;
  width: calc(100% / 7);
  text-align: left;
  color: #999;
  font-size: 12px;
  text-align: center;
  border-radius: 5px;
}

.current.today {
  background: rgb(242 242 242);
}

.sec_cal .cal_wrap .dates {
  display: flex;
  flex-flow: wrap;
  height: 290px;

}

.sec_cal .cal_wrap .day:nth-child(7n -1) {
  color: #3c6ffa;
}

.sec_cal .cal_wrap .day:nth-child(7n) {
  color: #ed2a61;
}

.sec_cal .cal_wrap .day.disable {
  color: #ddd;
}

.incom {
  color: blue;
  font-size: 90%;
  margin-top: 3px;
  margin-bottom: -18px;
}

.spend {
  color: red;
  font-size: 90%;
  margin-top: 20px;
}

#calBtn {
  width: 42px;
  margin: 3px;
}

#addBtn {
  width: 57px;
  margin-top: -18px;
  margin-left: 5px;
  margin-right: 5px;
}


#subTitle {
  font-size: 110%;
  margin-bottom: 3px;
}
</style>