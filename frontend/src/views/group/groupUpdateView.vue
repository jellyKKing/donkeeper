<template>
    <div>
        <div>
            <h2>그룹 수정</h2>
        </div>

        <div style="display:flex;">
            <div style="font-size:20px; width: 110px; display: flex; margin-top: 3px;">
                <label for="groupName">그룹 이름</label>
            </div>
            <div>
                <input type="text" id="groupName" v-model="groupName" style="border-radius:5px; width: 150px;  height:20px; margin: 5px;">
            </div>
            <button @click="existsGroupName"
            style="background-color:white; border-radius:5px; height:30px; margin-left:5px; margin-top:2px; padding:4px;">중복 체크</button>
        </div>



        <div style="display:flex;">
            <div style="font-size:20px; width: 110px; display: flex; margin-top: 3px;">
                <label for="email">팀원 추가</label>
            </div>
            <div>
                <input type="text" id="email" v-model="email" style="border-radius:5px; width: 150px; height:20px; margin: 5px;">
            </div>
        </div>
        <ul>
                <li v-for="email in emails" :key="email.id" @click="addMemberId(email)" :style="{ color: email.isClicked ? 'blue' : 'black' }">
                    {{ email.email }}
                    {{ email.nickname }}
                </li>
            </ul>

        



        <div style="display:flex; justify-content:center;">
            <div>
                <button @click="updateGroupName" :disabled="isGroupCreatable"
                style="border:none; background-color:#5987DF; color:white; padding:10px 15px; font-size:15px; margin: 0px 10px; width: 100px; font-weight:bold; border-radius:8px;">이름 변경</button>
            </div>

            <div>
                <button @click="inviteMember" :disabled="memberIds.length <= 0"
                style="border:none; background-color:#5987DF; color:white; padding:10px 15px; font-size:15px; margin: 0px 10px; width: 100px; font-weight:bold; border-radius:8px;">초대</button>
            </div>
        </div>
        
    </div>
  </template>
  
  <script>
  import { debounce } from 'lodash';

  export default {
    data() {
      return {
        groupName: '',
        email: '',
        emails : [],
        memberIds : [],
        existsCircleName : true,
        isChecked : false,
      }
    },
    computed: {
        isGroupCreatable() {
            return this.groupName == '' || this.existsCircleName;
        },
    },
    methods : {
        existsGroupName () {
            this.axios.get(process.env.VUE_APP_API_URL + `/circle/exists?name=` + this.groupName)
            .then((res) => {
                console.log(res.data);
                this.existsCircleName = res.data;
                if (res.data) {
                    alert("이미 사용 중인 그룹 이름입니다. 다시 체크해주세요.");
                }else {
                    alert("사용 가능합니다.");
                }
            })
        },
        searchEmail () {
            this.axios.get(process.env.VUE_APP_API_URL + `/member/search?email=` + this.email)
            .then((res) => {
                // this.emails = res.data;
                this.emails = res.data.map(email => {
                    return {
                    id: email.id,
                    email: email.email,
                    nickname: email.nickname,
                    isClicked: false
                    };
                });
                console.log(res.data);
            })
        },
        addMemberId(email) {
            if (this.memberIds.includes(email.id)) {
                this.memberIds = this.memberIds.filter(id => id !== email.id);
                return email.isClicked = false;
            } else {
                this.memberIds.push(email.id);
                return email.isClicked = true;
            }
        },
        updateGroupName(){
            this.axios({
                method: 'patch',
                url: process.env.VUE_APP_API_URL + `/circle?circleId=${this.$route.params.id}&name=${this.groupName}`,
            })
            .then((res) => {
                console.log(res.data);
                this.$router.push('/group');
            })
            .catch(err => { console.log(err) })
        },
        inviteMember () {
            this.axios({
                method: 'patch',
                url: process.env.VUE_APP_API_URL + `/circle/invite?circleId=${this.$route.params.id}`,
                data: {
                    "name" : this.groupName,
                    "member_ids" : this.memberIds
                },
            })
            .then((res) => {
                console.log(res.data);
                this.$router.push('/group');
            })
            .catch(err => { console.log(err) })
        }
    },
    watch: {
        email: debounce(function() {
            this.searchEmail();
        }, 500) // 500ms 동안 새로운 요청이 없으면 searchEmail 함수를 실행합니다.
        ,
        groupName () {
            this.existsCircleName = true;
        }
    },
    mounted () {
        this.axios.get(process.env.VUE_APP_API_URL + `/circle/${this.$route.params.id}/${new Date().getFullYear()}/${new Date().getMonth()+1}`)
        .then((res) => {
            this.groupName = res.data.name;
        })
    }
    
  
  }
  </script>
  
  <style></style>