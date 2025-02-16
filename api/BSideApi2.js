import request from '@/utils/PythonRequest'

export function MediaTalk(data){
    return request({
        url: '/mediaTalk',
        method: 'POST',
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        data
    })
}

export function PhotoTalk(data){
    return request({
        url: '/photoTalk',
        method: 'POST',
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        data
    })
}

export function VideoCreate(data){
    return request({
        url: '/videoCreate',
        method: 'POST',
        data
    })
}

export function SelectVideo(data){
    return request({
        url: '/selectVideo',
        method: 'POST',
        data
    })
}
